package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.entity.EnderEntity;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

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
                  ItemStack backStack = BackData.this.getStack();
                  if (backStack.getItem() instanceof EnderBackpack enderBackpack) {
                        UUID uuid = enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                        return ServerSave.getEnderData(uuid).getItemStacks();
                  }
                  return this.itemStacks;
            }

            @Override public Traits.LocalData getLocalData() {
                  return localData;
            }

            @Override
            public UUID getPlacedBy() {
                  if (backStack.getItem() instanceof EnderBackpack enderBackpack)
                        return enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                  return owner.getUUID();
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

      public CompoundTag getTrim() {
            if (backStack.getItem() instanceof EnderBackpack enderBackpack) {
                  UUID uuid = enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                  return ServerSave.getEnderData(uuid).getTrim();
            }
            return localData.trim;
      }

      public void set(ItemStack stack) {
            Services.COMPAT.setBackSlotItem(this, stack);
            backSlot.set(stack);
      }

      public void update(ItemStack stack) {
            if (stack.getItem() instanceof EnderBackpack enderBackpack)
                  enderBackpack.getOrCreateUUID(owner.getUUID(), stack);
            this.backStack = stack;
            this.localData = Traits.LocalData.fromstack(stack);
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

      public boolean backSlotDisabled() {
            if (owner.isCreative() && !Constants.SLOTS_MOD_ACTIVE)
                  return true;

            NonNullList<ItemStack> equipped = owner.getInventory().armor;
            return Services.COMPAT.backSlotDisabled(owner) || equipped.stream().anyMatch(stack -> !stack.isEmpty() && Constants.elytraOrDisables(stack.getItem()));
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
            float yaw = owner.yBodyRot + 180;

            int x = blockPos.getX();
            double y = blockPos.getY() + 1.5;
            int z = blockPos.getZ();

            Direction direction = Direction.UP;

            BackpackEntity backpackEntity = backStack.getItem() instanceof EnderBackpack enderBackpack ?
                        new EnderEntity(owner, x, y, z, direction, localData, yaw, enderBackpack.getOrCreateUUID(owner.getUUID(), backStack)) :
                        new BackpackEntity(owner, x, y, z, direction, localData, itemStacks, yaw);

            set(ItemStack.EMPTY);
      }

      public void copyTo(BackData newBackData) {
            newBackData.set(this.backStack);

            NonNullList<ItemStack> stacks = this.backpackInventory.getItemStacks();
            newBackData.backpackInventory.getItemStacks().addAll(stacks);
      }
}
