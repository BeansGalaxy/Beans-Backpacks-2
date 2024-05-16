package com.beansgalaxy.backpacks.inventory;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.IConfig;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.clientbound.SendViewers;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;

import java.util.List;
import java.util.UUID;

public abstract class BackpackInventory implements Container {

      public abstract EntityAccess getOwner();

      public abstract Traits.LocalData getTraits();

      public abstract UUID getPlacedBy();

      public abstract Level level();

      public boolean stopHopper() {
            return false;
      }

      private final NonNullList<ServerPlayer> playersViewing = NonNullList.create();
      public NonNullList<ServerPlayer> getPlayersViewing() {
            return playersViewing;
      }

      private final NonNullList<ItemStack> itemStacks = NonNullList.create();
      public NonNullList<ItemStack> getItemStacks() {
            return itemStacks;
      }

      private final Viewable viewable = new Viewable();
      public Viewable getViewable() {
            return viewable;
      }

      public void clearViewers() {
            getPlayersViewing().clear();
            getViewable().setViewers((byte) 0);
      }

      public void addViewer(ServerPlayer viewer) {
            getPlayersViewing().add(viewer);
            updateViewers();
      }

      public void removeViewer(ServerPlayer viewer) {
            getPlayersViewing().remove(viewer);
            updateViewers();
      }

      public void updateViewers() {
            NonNullList<ServerPlayer> playersViewing = getPlayersViewing();
            getViewable().setViewers((byte) Math.min(playersViewing.size(), Byte.MAX_VALUE));
            SendViewers.send(this);
      }

      public static boolean yawMatches(float viewerYaw, float ownerYaw, double acceptableYaw) {
            double yaw = Math.abs(viewerYaw - ownerYaw) % 360 - 180;
            return Math.abs(yaw) > 180 - acceptableYaw;
      }

      /**
       * CONTAINER METHODS BELOW
       **/

      public void playSound(PlaySound sound) {
            playSound(sound, 0.8f);
      }

      public void playSound(PlaySound sound, float volume) {
            if (getOwner() instanceof Entity entity) {
                  sound.at(entity, getTraits().sound(), volume);
            }
      }

      @Override
      public int getContainerSize() {
            return getItemStacks().size();
      }

      @Override
      public boolean isEmpty() {
            return getItemStacks().isEmpty()
            || Kind.is(getTraits().kind, Kind.POT, Kind.CAULDRON)
            || getItemStacks().stream().allMatch(ItemStack::isEmpty);
      }

      @Override
      public void clearContent() {
            if (Kind.ENDER.is(getTraits().kind)) return;
            this.getItemStacks().clear();
      }

      @Override
      public ItemStack getItem(int slot) {
            return getContainerSize() > slot ? this.getItemStacks().get(slot) : ItemStack.EMPTY;
      }

      @Override
      public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = removeItemSilent(slot, amount);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            return stack;
      }

      public ItemStack removeItemSilent(int slot, int amount) {
            ItemStack stack = getItem(slot).split(amount);
            if (stack.isEmpty()) {
                  if (getContainerSize() > slot)
                        getItemStacks().remove(slot);
            }
            setChanged();
            return stack;
      }

      @Override
      public int getMaxStackSize() {
            return Container.super.getMaxStackSize();
      }

      @Override
      public ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = removeItemSilent(slot);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            return stack;
      }

      public ItemStack removeItemSilent(int slot) {
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
      public void setItem(int slot, ItemStack stack) {
            int containerSize = getContainerSize();
            if (!stack.isEmpty())
                  if (getContainerSize() > slot)
                        getItemStacks().set(slot, stack);
                  else getItemStacks().add(slot, stack);
            else
                  if (containerSize > slot)
                        getItemStacks().remove(slot);
      }

      public ItemStack returnItem(int slot, ItemStack stack) {
            return returnItem(slot, stack, stack.getCount());
      }

      public ItemStack returnItem(int slot, ItemStack stack, int amount) {
            if (!stack.isEmpty())
                  return insertItem(stack, amount, slot);
            else
                  return removeItemNoUpdate(slot);
      }

      public ItemStack insertItem(ItemStack stack, int amount, int slot) {
            int insertedCount = stack.getCount();
            if (insertItemSilent(stack, amount, slot).getCount() != insertedCount)
                  playSound(stack.isEmpty() ? PlaySound.INSERT : PlaySound.TAKE);
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
      }

      public ItemStack insertItemSilent(ItemStack stack, int amount, int slot) {
            if (stack.isEmpty() || !canPlaceItem(stack))
                  return stack;

            int weight = weightByItem(stack);
            Traits.LocalData traits = this.getTraits();
            if (weight == 0 || traits == null || Kind.is(traits.kind, Kind.POT, Kind.CAULDRON))
                  return stack;

            boolean isServerSide = !level().isClientSide();
            int spaceLeft = spaceLeft();
            int count = Math.min(spaceLeft / weight, amount);
            if (count > 0)
            {
                  if (isServerSide && stack.getItem() instanceof BackpackItem)
                        triggerAdvancements(SpecialCriterion.Special.LAYERED);
                  this.getItemStacks().add(slot, stack.copyWithCount(count));
                  mergeItems();
                  stack.setCount(stack.getCount() - count);
                  setChanged();
            }
            if (isServerSide && Kind.LEATHER.is(traits.kind) && spaceLeft - (weight * amount) < 1)
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

      public int spaceLeft() {
            if (getTraits().kind == null)
                  return 0;

            int weight = 0;
            for (ItemStack stack : getItemStacks())
                  weight += weightByItem(stack) * stack.getCount();

            return this.getTraits().maxStacks() * 64 - weight;
      }

      public int weightByItem(ItemStack stack) {
            if (stack.is(Items.ENCHANTED_BOOK))
                  return 16;

            if (Kind.isBackpack(stack))
                  return 16;

            if (stack.isEmpty())
                  return 0;

            return 64 / stack.getMaxStackSize();
      };


      public void mergeItems() {
            for (int j = getItemStacks().size() - 2; j > -1; j--) {
                  ItemStack lookSlot = getItem(j);
                  for (int i = 0; i < getItemStacks().size(); i++) {
                        if (i == j) continue;
                        ItemStack compare = getItem(i);
                        if (ItemStack.isSameItemSameTags(lookSlot, compare)) {
                              lookSlot.grow(compare.getCount());
                              compare.setCount(0);
                        }
                  }
            }

            getItemStacks().removeIf(ItemStack::isEmpty);
      }

      public void readStackNbt(CompoundTag nbt) {
            clearContent();
            readStackNbt(nbt, this.getItemStacks());
      }

      public static void readStackNbt(CompoundTag nbt, NonNullList<ItemStack> itemStacks) {
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

      public void writeNbt(CompoundTag nbt) {
            writeNbt(nbt, getItemStacks());
      }

      public static void writeNbt(CompoundTag nbt, NonNullList<ItemStack> stacks) {
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


      public boolean canPlaceItem(ItemStack inserted) {
            if (IConfig.blacklistedItem(inserted.getItem()))
                  return false;

            boolean isEmpty = getItemStacks().isEmpty();
            ItemStack topStack = isEmpty ? ItemStack.EMPTY : getItemStacks().get(0);
//            if (this.getData().isPot()) TODO: TEST IF PLACE ITEM IN INSLOT IS BROKEN
//                  return isEmpty || inserted.is(topStack.getItem());

            boolean isFull = spaceLeft() < 1;
            return isEmpty || !isFull;
      }

      @Override
      public void setChanged() {

      }

      @Override
      public boolean stillValid(Player viewer) {
            EntityAccess owner = getOwner();
            if (owner instanceof Entity entity) {
                  return !entity.isRemoved() && viewer.distanceTo(entity) < 5f;
            }
            return true;
      }

      @Override
      public void stopOpen(Player player) {
            if (player instanceof ServerPlayer serverPlayer)
                  removeViewer(serverPlayer);
            if (getViewable().getViewers() < 1)
                  playSound(PlaySound.CLOSE);
      }

      public static BackpackInventory get(EntityAccess entity) {
            if (entity instanceof EntityAbstract backpack)
                  return backpack.getInventory();
            if (entity instanceof Player player)
                  return BackData.get(player).getBackpackInventory();
            if (entity instanceof EnderInventory ender)
                  return ender;

            return null;
      }

      public MenuProvider getMenuProvider() {
            return Services.NETWORK.getMenuProvider(getOwner());
      }

      public boolean hopperTakeOne(Container hopper) {
            if (isEmpty() || stopHopper())
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

      public boolean hopperInsertOne(Container hopper) {
            if (stopHopper()) return false;

            for (int i = 0; i < hopper.getContainerSize(); i++) {
                  ItemStack hopperItem = hopper.getItem(i);
                  if (!hopperItem.isEmpty()) {
                        insertItem(hopperItem, 1, 0);
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
