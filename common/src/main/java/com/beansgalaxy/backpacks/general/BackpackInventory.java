package com.beansgalaxy.backpacks.general;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface BackpackInventory extends Container {

      NonNullList<Player> getPlayersViewing();

      Data data = new Data();

      class Data {
            public String key;
            public int maxStacks;
            public String name;
            public Kind kind;
            public float headPitch = 0;
            public byte viewers = 0;

            public Data copy() {
                  Data data = new Data();
                  data.update(this.key, this.name, this.kind, this.maxStacks);
                  data.headPitch = this.headPitch;
                  this.viewers = 0;
                  return data;
            }

            public void update(Data data) {
                  this.update(data.key, data.name, data.kind, data.maxStacks);
            }

            public void update(String key, String name, Kind kind, int maxStacks) {
                  this.key = key;
                  this.name = name;
                  this.kind = kind;
                  this.maxStacks = maxStacks;
            }
      }

      default void clearViewers() {
            getPlayersViewing().clear();
            data.viewers = 0;
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
            data.viewers = (byte) Math.min(playersViewing.size(), Byte.MAX_VALUE);
            //if (!getOwner().level().isClientSide) TODO: IMPLEMENT NETWORKING
                  //Services.NETWORK.SyncBackpackViewersPacket(getOwner(), newViewers);
      }

      static boolean yawMatches(float viewerYaw, float ownerYaw, double acceptableYaw) {
            double yaw = Math.abs(viewerYaw - ownerYaw) % 360 - 180;
            return Math.abs(yaw) > 180 - acceptableYaw;
      }

      default boolean isOpen() {
            return data.viewers > 0;
      }

      default void updateOpen() {
            float newPitch = data.headPitch;
            boolean isOpen = isOpen();

            float speed = Math.max((-Math.abs(newPitch + .4F) + .6F) / 5, isOpen ? 0 : 0.1F);
            if (isOpen) speed /= -2;
            newPitch += speed;
            if (newPitch > 0) newPitch = 0;
            if (newPitch < -1) newPitch = -1;

            //newPitch = -1f; // HOLDS TOP OPEN FOR TEXTURING
            this.data.headPitch = newPitch;
      }

      /**
       * CONTAINER METHODS BELOW
       **/


      NonNullList<ItemStack> getItemStacks();

      void playSound(PlaySound sound);

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
            else if (getContainerSize() > slot) getItemStacks().remove(slot);
      }

      default ItemStack returnItem(int slot, ItemStack stack) {
            if (!stack.isEmpty()) {
                  return insertItem(stack, stack.getCount());
            } else return removeItemNoUpdate(slot);
      }

      default ItemStack insertItem(ItemStack stack, int amount) {
            ItemStack insertedStack = stack.copy();
            if (insertItemSilent(stack, amount) != insertedStack) {
                  playSound(stack.isEmpty() ? PlaySound.INSERT : PlaySound.TAKE);
            }
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
      }

      default ItemStack insertItemSilent(ItemStack stack, int amount) {
            int count = Math.min(amount, spaceLeft());
            if (!stack.isEmpty() && count > 0 && canPlaceItem(stack)) {
                  this.getItemStacks().add(0, mergeItem(stack.copyWithCount(count)));
                  stack.setCount(stack.getCount() - count);
            }
            return stack;
      }

      default int spaceLeft() {
            int totalWeight = this.getItemStacks().stream().mapToInt(
                        itemStacks -> weightByItem(itemStacks) * itemStacks.getCount()).sum();
            return data.kind == null ? 0 : (data.maxStacks * 64) - totalWeight;
      }

      default int weightByItem(ItemStack stack) {
            return 64 / stack.getMaxStackSize();
      };


      private ItemStack mergeItem(ItemStack stack) {
            for (int i = 0; i <= getItemStacks().size(); i++) {
                  ItemStack lookSlot = getItem(i);
                  if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, lookSlot)) {
                        int count = stack.getCount() + lookSlot.getCount();
                        int maxCount = data.kind == Kind.POT ? Integer.MAX_VALUE : stack.getMaxStackSize();
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


      default boolean canPlaceItem(ItemStack stack) {
            boolean isEmpty = getItemStacks().isEmpty();
            ItemStack stack1 = isEmpty ? ItemStack.EMPTY : getItemStacks().get(0);
            boolean sameStack = !stack.is(stack1.getItem());
            boolean isPot = data.kind == Kind.POT;
            boolean isFull = spaceLeft() < 1;
            if (!isEmpty && isPot && sameStack || isFull)
                  return false;
            return true;
      }

      @Override
      default void setChanged() {
      }


      @Override
      default boolean stillValid(Player player) {
            return false;
      }
}
