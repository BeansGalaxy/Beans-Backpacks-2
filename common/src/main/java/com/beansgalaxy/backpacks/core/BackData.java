package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackData {
      public final Player owner;
      public final BackpackInventory backpackInventory = new BackpackInventory() {

            @Override public Entity getOwner() {
                  return owner;
            }

            public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();

            @Override public Viewable getViewable() {
                  return viewable;
            }

            NonNullList<ServerPlayer> playersViewing = NonNullList.create();

            @Override public NonNullList<ServerPlayer> getPlayersViewing() {
                  return playersViewing;
            }

            private final NonNullList<ItemStack> itemStacks = NonNullList.create();

            @Override public NonNullList<ItemStack> getItemStacks() {
                  return this.itemStacks;
            }

            @Override public Traits.LocalData getLocalData() {
                  return localData;
            }
      };

      protected static final int[] UV = {59, 62};
      public final BackSlot backSlot = new BackSlot(this);
      public final InSlot inSlot = new InSlot(this);
      public boolean actionKeyPressed = false;
      private ItemStack backStack = ItemStack.EMPTY;
      private Traits.LocalData localData = Traits.LocalData.EMPTY;

      public BackData(Player owner) {
            this.owner = owner;
      }

      public static BackData get(Player player) {
            return ((BackAccessor) player.getInventory()).getBackData();
      }

      public ItemStack getStack() {
            return Services.COMPAT.getBackSlotItem(this, backStack);
      }

      public Traits.LocalData getLocalData() {
            return localData;
      }

      public boolean isEmpty() {
            return backStack.isEmpty();
      }

      public void set(ItemStack backStack) {
            Services.COMPAT.setBackSlotItem(this, backStack);
            backSlot.set(backStack);
      }

      public void update(ItemStack backStack) {
            this.backStack = backStack;
            this.localData = Traits.LocalData.fromStack(backStack);
            this.setChanged();
      }

      public void setChanged() {
            ItemStack stack = this.getStack();
            if (stack.isEmpty())
                  backpackInventory.clearViewers();
            if (owner instanceof ServerPlayer serverPlayer) {
                  Services.REGISTRY.triggerEquipAny(serverPlayer);
                  Services.NETWORK.SyncBackSlot(serverPlayer);
            }
      }

      public void drop() {
            if (localData.key.isEmpty())
                  return;

            NonNullList<ItemStack> itemStacks = backpackInventory.getItemStacks();

            ItemStack backStack = getStack();
            if (!Kind.isBackpack(backStack)) {
                  owner.spawnAtLocation(backStack.copy(), 0.5f);
                  if (localData.isPot()) {
                        int iteration = 0;
                        int maxIterations = 108;
                        while (!itemStacks.isEmpty() && iteration < maxIterations) {
                              ItemStack stack = itemStacks.remove(iteration);
                              if (stack.getMaxStackSize() == 64) {
                                    owner.spawnAtLocation(stack, 0.5f);
                              } else while (stack.getCount() > 0) {
                                    int removedCount = Math.min(stack.getCount(), stack.getMaxStackSize());
                                    owner.spawnAtLocation(stack.copyWithCount(removedCount));
                                    stack.shrink(removedCount);
                              }
                              iteration++;
                        }
                        SoundEvent soundEvent = iteration >= maxIterations ? SoundEvents.DECORATED_POT_BREAK : SoundEvents.DECORATED_POT_SHATTER;
                        owner.playSound(soundEvent, 0.4f, 0.8f);
                  }
                  return;
            }

            BlockPos blockPos = owner.getOnPos();
            float yRot = owner.yBodyRot + 180;

            int x = blockPos.getX();
            double y = blockPos.getY() + 1.5;
            int z = blockPos.getZ();

            new BackpackEntity(owner, owner.level(), x, y, z, Direction.UP,
                        localData, itemStacks, yRot);
      }

      public void copyTo(BackData newBackData) {
            newBackData.set(this.backStack);

            NonNullList<ItemStack> stacks = this.backpackInventory.getItemStacks();
            newBackData.backpackInventory.getItemStacks().addAll(stacks);
      }
}
