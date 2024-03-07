package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

public interface BackpackInventory extends Container {

      Viewable getViewable();

      Entity getOwner();

      Traits.LocalData getLocalData();

      UUID getPlacedBy();

      class Viewable {
            public float headPitch = 0;
            public byte viewers = 0;

            boolean isOpen() {
                  return viewers > 0;
            }

            public void updateOpen() {
                  float opened = 0.55f;
                  float newPitch = headPitch;
                  float speed = 8f;

                  int j = isOpen() ? -1 : 3;
                  float i = (newPitch + opened + (Math.abs(j) / 10f)) * .1f;
                  i *= i;
                  newPitch += i * speed * j;

                  if (newPitch < -opened)
                        newPitch = -opened;
                  if (newPitch > 0)
                        newPitch = 0;

                  //newPitch = -1f; // HOLDS TOP OPEN FOR TEXTURING
                  headPitch = newPitch;
            }
      }

      NonNullList<ServerPlayer> getPlayersViewing();

      default void clearViewers() {
            getPlayersViewing().clear();
            getViewable().viewers = 0;
      }

      default void addViewer(ServerPlayer viewer) {
            getPlayersViewing().add(viewer);
            updateViewers();
      }

      default void removeViewer(ServerPlayer viewer) {
            getPlayersViewing().remove(viewer);
            updateViewers();
      }

      default void updateViewers() {
            NonNullList<ServerPlayer> playersViewing = getPlayersViewing();
            getViewable().viewers = (byte) Math.min(playersViewing.size(), Byte.MAX_VALUE);
            Services.NETWORK.SyncViewers(getOwner(), getViewable().viewers);
      }

      static boolean yawMatches(float viewerYaw, float ownerYaw, double acceptableYaw) {
            double yaw = Math.abs(viewerYaw - ownerYaw) % 360 - 180;
            return Math.abs(yaw) > 180 - acceptableYaw;
      }

      /**
       * CONTAINER METHODS BELOW
       **/
      NonNullList<ItemStack> getItemStacks();

      default void playSound(PlaySound sound) {
            sound.at(getOwner(), getLocalData().kind());
      }

      @Override
      default int getContainerSize() {
            return getItemStacks().size();
      }

      @Override
      default boolean isEmpty() {
            return getItemStacks().isEmpty() || getItemStacks().stream().allMatch(ItemStack::isEmpty);
      }

      @Override
      default void clearContent() {
            this.getItemStacks().clear();
      }

      @Override
      default ItemStack getItem(int slot) {
            return getContainerSize() > slot ? this.getItemStacks().get(slot) : ItemStack.EMPTY;
      }

      @Override
      default ItemStack removeItem(int slot, int amount) {
            ItemStack stack = removeItemSilent(slot, amount);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            return stack;
      }

      default ItemStack removeItemSilent(int slot, int amount) {
            ItemStack stack = getItem(slot).split(amount);
            if (stack.isEmpty()) {
                  if (getContainerSize() > slot)
                        getItemStacks().remove(slot);
            }
            setChanged();
            return stack;
      }

      @Override
      default int getMaxStackSize() {
            return Container.super.getMaxStackSize();
      }

      @Override
      default ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = removeItemSilent(slot);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            return stack;
      }

      default ItemStack removeItemSilent(int slot) {
            ItemStack returned = ItemStack.EMPTY;
            if (getContainerSize() > slot) {
                  ItemStack stack = getItemStacks().get(slot);
                  int maxCount = stack.getMaxStackSize();
                  if (stack.getCount() > maxCount) {
                        stack.shrink(maxCount);
                        returned = stack.copyWithCount(maxCount);
                  } else
                        returned = this.getItemStacks().remove(slot);
                  setChanged();
            }
            return returned;
      }

      @Override
      default void setItem(int slot, ItemStack stack) {
            int containerSize = getContainerSize();
            if (!stack.isEmpty())
                  if (getContainerSize() > slot)
                        getItemStacks().set(slot, stack);
                  else getItemStacks().add(slot, stack);
            else
                  if (containerSize > slot)
                        getItemStacks().remove(slot);
      }

      default ItemStack returnItem(int slot, ItemStack stack) {
            return returnItem(slot, stack, stack.getCount());
      }

      default ItemStack returnItem(int slot, ItemStack stack, int amount) {
            if (!stack.isEmpty())
                  return insertItem(stack, amount);
            else
                  return removeItemNoUpdate(slot);
      }

      default ItemStack insertItem(ItemStack stack, int amount) {
            int insertedCount = stack.getCount();
            if (insertItemSilent(stack, amount).getCount() != insertedCount)
                  playSound(stack.isEmpty() ? PlaySound.INSERT : PlaySound.TAKE);
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
      }

      default ItemStack insertItemSilent(ItemStack stack, int amount) {
            if (stack.isEmpty() || !canPlaceItem(stack))
                  return stack;

            int weight = weightByItem(stack);
            Traits.LocalData localData = getLocalData();
            if (weight == 0 || localData == null || localData.key.isEmpty())
                  return stack;

            boolean isServerSide = !getOwner().level().isClientSide();
            int spaceLeft = spaceLeft();
            int count = Math.min(spaceLeft / weight, amount);
            if (count > 0)
            {
                  if (isServerSide && stack.getItem() instanceof BackpackItem)
                        triggerAdvancements(SpecialCriterion.Special.LAYERED);
                  this.getItemStacks().add(0, mergeItem(stack.copyWithCount(count)));
                  stack.setCount(stack.getCount() - count);
                  setChanged();
            }
            if (isServerSide && Objects.equals(localData.key, "leather") && spaceLeft - weight < 1)
                  triggerAdvancements(SpecialCriterion.Special.FILLED_LEATHER);

            return stack;
      }

      private void triggerAdvancements(SpecialCriterion.Special special) {
            if (getOwner() instanceof ServerPlayer thisPlayer)
                  Services.REGISTRY.triggerSpecial(thisPlayer, special);
            for (ServerPlayer otherPlayers : getPlayersViewing())
                  Services.REGISTRY.triggerSpecial(otherPlayers, special);
      }

      private void triggerHopper() {
            if (getOwner() instanceof EntityAbstract entityAbstract) {
                  if (entityAbstract.level().getPlayerByUUID(entityAbstract.getPlacedBy()) instanceof ServerPlayer serverPlayer) {
                        Services.REGISTRY.triggerSpecial(serverPlayer, SpecialCriterion.Special.HOPPER);
                        if (entityAbstract instanceof EntityEnder)
                              Services.REGISTRY.triggerSpecial(serverPlayer, SpecialCriterion.Special.ENDER_HOPPER);
                  }
            }
      }

      default int spaceLeft() {
            if (getLocalData().kind() == null)
                  return 0;

            int totalWeight = this.getItemStacks().stream().mapToInt(
                        itemStacks -> weightByItem(itemStacks) * itemStacks.getCount()).sum();

            return getLocalData().maxStacks() * 64 - totalWeight;
      }

      default int weightByItem(ItemStack stack) {
            return 64 / stack.getMaxStackSize();
      };


      private ItemStack mergeItem(ItemStack stack) {
            for (int i = 0; i <= getItemStacks().size(); i++) {
                  ItemStack lookSlot = getItem(i);
                  if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, lookSlot)) {
                        int count = stack.getCount() + lookSlot.getCount();
                        int maxCount = getLocalData().isPot() ? Integer.MAX_VALUE : stack.getMaxStackSize();
                        if (count > maxCount) {
                              lookSlot.setCount(maxCount);
                              count -= maxCount;
                        } else getItemStacks().remove(i);
                        stack.setCount(count);
                  }
            }
            return stack;
      }

      default void readStackNbt(CompoundTag nbt) {
            readStackNbt(nbt, this.getItemStacks());
      }

      static void readStackNbt(CompoundTag nbt, NonNullList<ItemStack> itemStacks) {
            itemStacks.clear();
            ListTag nbtList = nbt.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < nbtList.size(); ++i) {
                  CompoundTag nbtCompound = nbtList.getCompound(i);

                  ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(
                              new ResourceLocation(nbtCompound.getString("id"))), nbtCompound.getInt("Count"));
                  if (nbtCompound.contains("tag", Tag.TAG_COMPOUND)) {
                        stack.setTag(nbtCompound.getCompound("tag"));
                        stack.getItem().verifyTagAfterLoad(stack.getTag());
                  }
                  if (stack.isDamageableItem()) {
                        stack.setDamageValue(stack.getDamageValue());
                  }

                  if (!stack.isEmpty())
                        itemStacks.add(stack);
            }

      }

      default void writeNbt(CompoundTag nbt) {
            writeNbt(nbt, getItemStacks());
      }

      static void writeNbt(CompoundTag nbt, NonNullList<ItemStack> stacks) {
            ListTag nbtList = new ListTag();
            for (int i = 0; i < stacks.size(); ++i) {
                  ItemStack itemStack = stacks.get(i);
                  if (itemStack.isEmpty()) continue;
                  CompoundTag nbtCompound = new CompoundTag();
                  nbtCompound.putByte("Slot", (byte)i);

                  ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                  nbtCompound.putString("id", identifier.toString());
                  nbtCompound.putInt("Count", itemStack.getCount());
                  if (itemStack.getTag() != null) {
                        nbtCompound.put("tag", itemStack.getTag().copy());
                  }

                  nbtList.add(nbtCompound);
            }
            if (!nbtList.isEmpty() || stacks.isEmpty()) {
                  nbt.put("Items", nbtList);
            }
      }


      default boolean canPlaceItem(ItemStack inserted) {
            if (Constants.BLACKLIST_ITEMS.contains(inserted.getItem()))
                  return false;

            boolean isEmpty = getItemStacks().isEmpty();
            ItemStack topStack = isEmpty ? ItemStack.EMPTY : getItemStacks().get(0);
            if (getLocalData().isPot())
                  return isEmpty || inserted.is(topStack.getItem());

            boolean isFull = spaceLeft() < 1;
            return isEmpty || !isFull;
      }

      @Override
      default void setChanged() {
      }


      @Override
      default boolean stillValid(Player viewer) {
            Entity owner = getOwner();
            return !owner.isRemoved() && viewer.distanceTo(owner) < 5f;
      }

      @Override
      default void stopOpen(Player player) {
            if (player instanceof ServerPlayer serverPlayer)
                  removeViewer(serverPlayer);
            if (getViewable().viewers < 1)
                  playSound(PlaySound.CLOSE);
      }

      static BackpackInventory get(Entity entity) {
            if (entity instanceof EntityAbstract backpack)
                  return backpack.getInventory();
            if (entity instanceof Player player)
                  return BackData.get(player).backpackInventory;
            return null;
      }

      default MenuProvider getMenuProvider() {
            return Services.NETWORK.getMenuProvider(getOwner());
      }

      default boolean hopperTakeOne(Container hopper) {
            if (isEmpty())
                  return false;

            for (int i = 0; i < hopper.getContainerSize(); i++) {
                  ItemStack hopperItem = hopper.getItem(i);
                  int matchSlot = matchesSlot(hopperItem);
                  if (matchSlot != -1 && hopperItem.getCount() < hopperItem.getMaxStackSize() && !getItem(matchSlot).isEmpty()) {
                        hopperItem.grow(1);
                        removeItemSilent(matchSlot, 1);
                        triggerHopper();
                        return true;
                  }
                  if (hopperItem.isEmpty() && !isEmpty()) {
                        hopper.setItem(i, removeItemSilent(getContainerSize() - 1, 1));
                        triggerHopper();
                        return true;
                  }
            }
            return false;
      }

      default boolean hopperInsertOne(Container hopper) {
            for (int i = 0; i < hopper.getContainerSize(); i++) {
                  ItemStack hopperItem = hopper.getItem(i);
                  if (!hopperItem.isEmpty()) {
                        insertItem(hopperItem, 1);
                        triggerHopper();
                        return true;
                  }
            }
            return false;
      }

      private int matchesSlot(ItemStack stack) {
            for (int j = getContainerSize() -1; j >= 0; j--) {
                  ItemStack item = getItem(j);
                  if (ItemStack.isSameItemSameTags(item, stack))
                        return j;
            }
            return -1;
      }

}
