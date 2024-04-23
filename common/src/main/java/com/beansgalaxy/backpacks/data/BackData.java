package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.screen.InSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
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
                  if (!backStack.isEmpty() && backStack.getItem() instanceof EnderBackpack enderBackpack) {
                        UUID uuid = enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                        return EnderStorage.getEnderData(uuid, owner.level()).getItemStacks();
                  }
                  return this.itemStacks;
            }

            @Override
            public Traits.LocalData getTraits() {
                  return traits;
            }

            @Override
            public UUID getPlacedBy() {
                  if (backStack.getItem() instanceof EnderBackpack enderBackpack)
                        return enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                  return owner.getUUID();
            }

            @Override
            public void setChanged() {
                  ItemStack backStack = BackData.this.getStack();
                  if (!owner.level().isClientSide && backStack.getItem() instanceof EnderBackpack enderBackpack) {
                        UUID uuid = enderBackpack.getOrCreateUUID(owner.getUUID(), backStack);
                        EnderStorage.get().syncViewers(uuid);
                  }
                  BackpackInventory.super.setChanged();
            }
      };

      public static final int[] UV_SURVIVAL = {59, 62};
      public static final int[] UV_CREATIVE = Constants.SLOTS_MOD_ACTIVE ? new int[]{-2000, -2000} : new int[]{89, 33};
      public final BackSlot backSlot = new BackSlot(this);
      public final InSlot inSlot = new InSlot(this);
      private final HashSet<EnderStorage.PackagedLocation> enderLocations = new HashSet<>();
      private Traits.LocalData traits = Traits.LocalData.EMPTY;
      private ItemStack backStack = ItemStack.EMPTY;
      public boolean actionKeyPressed = false;

      public void setEnderLocations(HashSet<EnderStorage.PackagedLocation> newLocations) {
            this.enderLocations.clear();
            this.enderLocations.addAll(newLocations);
      }

      public HashSet<EnderStorage.PackagedLocation> getEnderLocations() {
            return new HashSet<>(enderLocations);
      }

      public BackData(Player owner) {
            this.owner = owner;
      }

      public static BackData get(Player player) {
            return ((BackAccessor) player.getInventory()).getBackData();
      }

      public ItemStack getStack() {
            ItemStack backSlotItem = Services.COMPAT.getBackSlotItem(this, backStack);
            return backSlotItem.isEmpty() ? ItemStack.EMPTY : backSlotItem;
      }

      public Traits.LocalData getTraits() {
            if (isEmpty())
                  traits = Traits.LocalData.EMPTY;
            return traits;
      }

      public boolean isEmpty() {
            return backStack.isEmpty();
      }

      public void set(ItemStack stack) {
            Services.COMPAT.setBackSlotItem(this, stack);
            backSlot.set(stack);
      }

      public void update(ItemStack stack) {
            if (stack.isEmpty())
                  backpackInventory.clearViewers();
            else if (stack.getItem() instanceof EnderBackpack enderBackpack) {
                  UUID uuid = enderBackpack.getOrCreateUUID(owner.getUUID(), stack);
                  EnderStorage.get().addViewer(uuid, backpackInventory);
            }

            this.backStack = stack;
            this.traits = Traits.LocalData.fromStack(stack);
      }

      public void setChanged() {
            if (owner instanceof ServerPlayer serverPlayer) {
                  SyncBackSlot.send(serverPlayer);
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
            return !getDisabling().isEmpty();
      }

      public List<ItemStack> getDisabling() {
            List<ItemStack> items = Services.COMPAT.backSlotDisabled(owner);
            items.removeIf(ItemStack::isEmpty);
            NonNullList<ItemStack> disabling = NonNullList.create();
            for (ItemStack stack : owner.getInventory().armor) {
                  if (!stack.isEmpty() && Constants.elytraOrDisables(stack.getItem()))
                        disabling.add(stack);
            }
            disabling.addAll(items);
            return disabling;
      }

      public void drop() {
            Level level = owner.level();
            boolean isCanceled = Services.COMPAT.invokeListenersOnDeath(this);
            if (isCanceled || ConfigHelper.keepBackSlot(level))
                  return;

            BlockPos blockPos = owner.getOnPos();
            float yaw = owner.yBodyRot + 180;
            int x = blockPos.getX();
            double y = blockPos.getY() + 1.5;
            int z = blockPos.getZ();

            Direction direction = Direction.UP;
            if (Services.COMPAT.graveModLoaded()) {
                  Direction step = Direction.fromYRot(yaw);
                  BlockPos relative = blockPos.relative(step);
                  y = owner.getY();
                  if (canPlaceBackpackSafely(owner, relative)) {
                        x = relative.getX();
                        z = relative.getZ();
                        direction = step;
                  } else
                        direction = Direction.fromYRot(yaw + 90);
            }

            ItemStack backStack = this.getStack();
            if (!Kind.isBackpack(backStack)) {
                  owner.spawnAtLocation(backStack.copy());
                  set(ItemStack.EMPTY);
                  return;
            }

            if (!level.isClientSide()) {
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  itemStacks.addAll(backpackInventory.getItemStacks());
                  EntityAbstract.create(backStack, x, y, z, yaw, true, direction, owner, itemStacks);
            }

            set(ItemStack.EMPTY);
      }

      private static boolean canPlaceBackpackSafely(Player player, BlockPos blockPos) {
            Level level = player.level();
            BlockState blockState = level.getBlockState(blockPos.above());
            BlockGetter chunkForCollisions = level.getChunk(blockPos);
            if (blockState.isCollisionShapeFullBlock(chunkForCollisions, blockPos.above()))
                  return false;

            BlockState blockStateBelow = level.getBlockState(blockPos);
            return blockStateBelow.entityCanStandOn(chunkForCollisions, blockPos, player);
      }

      public void copyTo(BackData newBackData) {
            newBackData.set(this.backStack);
            if (getStack().getItem() instanceof EnderBackpack)
                  return;

            NonNullList<ItemStack> stacks = this.backpackInventory.getItemStacks();
            newBackData.backpackInventory.getItemStacks().addAll(stacks);
      }
}
