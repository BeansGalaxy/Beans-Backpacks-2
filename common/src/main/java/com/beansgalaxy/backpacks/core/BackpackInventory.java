package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.List;

public interface BackpackInventory extends Container {

      Viewable getViewable();

      Entity getOwner();

      LocalData getLocalData();

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

      NonNullList<Player> getPlayersViewing();

      default void clearViewers() {
            getPlayersViewing().clear();
            getViewable().viewers = 0;
      }

      default void addViewer(Player viewer) {
            if (getPlayersViewing().stream().noneMatch(viewing -> viewing.equals(viewer))) {}
            getPlayersViewing().add(viewer);
            updateViewers();
      }

      default void removeViewer(Player viewer) {
            getPlayersViewing().remove(viewer);
            updateViewers();
      }

      default void updateViewers() {
            NonNullList<Player> playersViewing = getPlayersViewing();
            getViewable().viewers = (byte) Math.min(playersViewing.size(), Byte.MAX_VALUE);
            if (!getOwner().level().isClientSide)
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
            sound.at(getOwner());
      }

      @Override
      default int getContainerSize() {
            return getItemStacks().size();
      }

      @Override
      default boolean isEmpty() {
            return getItemStacks().isEmpty();
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
            List<ItemStack> stacks = getItemStacks();
            ItemStack stack = stacks.get(slot).copyWithCount(amount);
            stacks.get(slot).split(amount);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            if (stacks.get(slot).isEmpty())
                  stacks.remove(slot);
            return stack;
      }

      @Override
      default ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = removeItemSilent(slot);
            if (!stack.isEmpty())
                  playSound(PlaySound.TAKE);
            return stack;
      }

      default ItemStack removeItemSilent(int slot) {
            if (getContainerSize() > slot) {
                  ItemStack stack = getItemStacks().get(slot);
                  int maxCount = stack.getMaxStackSize();
                  if (stack.getCount() > maxCount) {
                        stack.shrink(maxCount);
                        return stack.copyWithCount(maxCount);
                  }
                  return this.getItemStacks().remove(slot);
            }
            return ItemStack.EMPTY;
      }

      @Override
      default void setItem(int slot, ItemStack stack) {
            if (!stack.isEmpty())
                  if (getContainerSize() > slot)
                        getItemStacks().set(slot, stack);
                  else getItemStacks().add(slot, stack);
            else
                  if (getContainerSize() > slot)
                        getItemStacks().remove(slot);
      }

      default ItemStack returnItem(int slot, ItemStack stack) {
            if (!stack.isEmpty())
                  return insertItem(stack);
            else
                  return removeItemNoUpdate(slot);
      }

      default ItemStack insertItem(ItemStack stack) {
            return insertItem(stack, stack.getCount());
      }

      default ItemStack insertItem(ItemStack stack, int amount) {
            ItemStack insertedStack = stack.copy();
            if (insertItemSilent(stack, amount) != insertedStack)
                  playSound(stack.isEmpty() ? PlaySound.INSERT : PlaySound.TAKE);
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
      }

      default ItemStack insertItemSilent(ItemStack stack, int amount) {
            if (!stack.isEmpty() && canPlaceItem(stack)) {
                  int space = spaceLeft();
                  int weight = weightByItem(stack);
                  int weightedSpace = space / weight;
                  int count = Math.min(weightedSpace, amount);
                  if (count > 0) {
                        this.getItemStacks().add(0, mergeItem(stack.copyWithCount(count)));
                        stack.setCount(stack.getCount() - count);
                  }
            }
            return stack;
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
                        int maxCount = getLocalData().kind() == Kind.POT ? Integer.MAX_VALUE : stack.getMaxStackSize();
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
            ListTag nbtList = nbt.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < nbtList.size(); ++i) {
                  CompoundTag nbtCompound = nbtList.getCompound(i);

                  ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(nbtCompound.getString("id"))), nbtCompound.getInt("Count"));
                  if (nbtCompound.contains("tag", Tag.TAG_COMPOUND)) {
                        stack.setTag(nbtCompound.getCompound("tag"));
                        stack.getItem().verifyTagAfterLoad(stack.getTag());
                  }
                  if (stack.isDamageableItem()) {
                        stack.setDamageValue(stack.getDamageValue());
                  }

                  this.getItemStacks().add(stack);
            }
      }

      default CompoundTag writeNbt(CompoundTag nbt, boolean setIfEmpty) {
            ListTag nbtList = new ListTag();
            NonNullList<ItemStack> stacks = getItemStacks();
            for (int i = 0; i < stacks.size(); ++i) {
                  ItemStack itemStack = stacks.get(i);
                  if (itemStack.isEmpty()) continue;
                  CompoundTag nbtCompound = new CompoundTag();
                  nbtCompound.putByte("Slot", (byte)i);

                  ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                  nbtCompound.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
                  nbtCompound.putInt("Count", itemStack.getCount());
                  if (itemStack.getTag() != null) {
                        nbtCompound.put("tag", itemStack.getTag().copy());
                  }

                  nbtList.add(nbtCompound);
            }
            if (!nbtList.isEmpty() || setIfEmpty) {
                  nbt.put("Items", nbtList);
            }
            return nbt;
      }


      default boolean canPlaceItem(ItemStack inserted) {
            if (inserted.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock)
                  return false;

            boolean isEmpty = getItemStacks().isEmpty();
            ItemStack topStack = isEmpty ? ItemStack.EMPTY : getItemStacks().get(0);
            if (getLocalData().kind() == Kind.POT)
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
            removeViewer(player);
            if (getViewable().viewers < 1)
                  PlaySound.CLOSE.at(getOwner());
      }

      static BackpackInventory get(Entity entity) {
            if (entity instanceof Backpack backpack)
                  return backpack.getBackpackInventory();
            if (entity instanceof Player player) {
                  BackSlot backSlot = BackSlot.get(player);
                  return backSlot.backpackInventory;
            }
            return null;
      }

      default MenuProvider getMenuProvider() {
            return Services.NETWORK.getMenuProvider(getOwner());
      }
}
