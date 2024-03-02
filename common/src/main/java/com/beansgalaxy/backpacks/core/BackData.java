package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.InSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
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
                        return EnderStorage.getEnderData(uuid, owner.level()).getItemStacks();
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

      public static final int[] UV = {59, 62};
      public final BackSlot backSlot = new BackSlot(this);
      public final InSlot inSlot = new InSlot(this);
      private final HashSet<EnderStorage.Location> enderLocations = new HashSet<>();
      private Traits.LocalData localData = Traits.LocalData.EMPTY;
      private ItemStack backStack = ItemStack.EMPTY;
      public boolean actionKeyPressed = false;

      public void setEnderLocations(HashSet<EnderStorage.Location> newLocations) {
            this.enderLocations.clear();
            this.enderLocations.addAll(newLocations);
      }

      public HashSet<EnderStorage.Location> getEnderLocations() {
            return new HashSet<>(enderLocations);
      }

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
                  return EnderStorage.getTrim(uuid, owner.level());
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

      public void playEquipSound(ItemStack stack) {
            Kind kind = Kind.fromStack(stack);
            if (owner.level().isClientSide() && kind != null) {
                  Tooltip.playSound(kind, PlaySound.EQUIP);
            }
            if (stack.getItem() instanceof Equipable equipable)
                  owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), equipable.getEquipSound(), owner.getSoundSource(), 1.0F, 1.0F);
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

            EntityAbstract.create(backStack, x, y, z, yaw, true, Direction.UP, owner, itemStacks);

            set(ItemStack.EMPTY);
      }

      public void copyTo(BackData newBackData) {
            newBackData.set(this.backStack);
            if (getStack().getItem() instanceof EnderBackpack)
                  return;

            NonNullList<ItemStack> stacks = this.backpackInventory.getItemStacks();
            newBackData.backpackInventory.getItemStacks().addAll(stacks);
      }
}
