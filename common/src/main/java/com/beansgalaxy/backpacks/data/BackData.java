package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.network.clientbound.SendBackSlot;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.screen.InSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BackData {
      public final Player owner;
      private final BackpackInventory backpackInventory = new BackpackInventory() {

            @Override public Entity getOwner() {
                  return owner;
            }

            @Override
            public Level level() {
                  return owner.level();
            }

            @Override
            public Traits.LocalData getTraits() {
                  return traits;
            }

            @Override
            public UUID getPlacedBy() {
                  return owner.getUUID();
            }
      };

      public static final int[] UV_SURVIVAL = {59, 62};
      public static final int[] UV_CREATIVE = Constants.SLOTS_MOD_ACTIVE ? new int[]{-2000, -2000} : new int[]{89, 33};
      public final BackSlot backSlot = new BackSlot(this);
      public final InSlot inSlot = new InSlot(this);
      private final HashSet<EnderStorage.PackagedLocation> enderLocations = new HashSet<>();
      private Traits.LocalData traits = Traits.LocalData.EMPTY;
      private ItemStack backStack = ItemStack.EMPTY;
      public boolean actionKeyDown = false;
      public boolean menusKeyDown = false;

      public void setEnderLocations(HashSet<EnderStorage.PackagedLocation> newLocations) {
            this.enderLocations.clear();
            this.enderLocations.addAll(newLocations);
      }

      public EnderStorage getEnderStorage() {
            return EnderStorage.get(owner.level());
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
            if (stack == null) return;
            Services.COMPAT.setBackSlotItem(this, stack);
            backSlot.set(stack);
      }

      public void update(ItemStack stack) {
            if (stack == null) return;
            if (stack.isEmpty())
                  getBackpackInventory().clearViewers();
            else if (stack.getItem() instanceof EnderBackpack enderBackpack) {
                  UUID uuid = enderBackpack.getOrCreateUUID(owner, stack);
                  getEnderStorage().addViewer(uuid, owner);
            }

            this.backStack = stack;
            this.traits = Traits.LocalData.fromStack(stack, owner);
      }

      public void setChanged() {
            if (owner instanceof ServerPlayer serverPlayer) {
                  SendBackSlot.send(serverPlayer);
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

      public boolean mayEquip(ItemStack toEquip) {
            return mayEquip(toEquip, true, true);
      }

      public boolean mayEquip(ItemStack toEquip, boolean inMenu, boolean showMessage) {
            if (toEquip.isEmpty())
                  return true;

            if (!Kind.isWearable(toEquip))
                  return false;

            ItemStack wornStack = this.getStack();
            boolean notEmpty = !wornStack.isEmpty();
            Component msg = null;
            if (toEquip.getItem() instanceof EnderBackpack ender) {
                  UUID uuid = ender.getOrCreateUUID(owner, toEquip);
                  EnderInventory enderData = EnderStorage.getEnderData(uuid, owner.level());
                  if (enderData.isLocked() && uuid != owner.getUUID()) {
                        msg = Component.translatable("entity.beansbackpacks.locked.is_locked");
                  }
            }

            if (msg == null && notEmpty) {
                  if (!inMenu && wornStack != toEquip)
                        msg = Component.translatable("entity.beansbackpacks.blocked.already_equipped",
                                    Constants.getName(wornStack));
                  else if (wornStack.is(Services.REGISTRY.getWinged()) && Constants.ELYTRA_ITEMS.contains(toEquip.getItem()))
                        msg = Component.translatable("entity.beansbackpacks.blocked.already_equipped",
                                    Constants.getName(wornStack));
            }

            if (msg == null) {
                  if (Kind.isWings(toEquip)) {
                        List<ItemStack> disabling = this.getDisabling();
                        Iterator<ItemStack> iterator = disabling.stream().filter(in -> !in.isEmpty() && Kind.isWings(in)).iterator();
                        if (iterator.hasNext()) {
                              MutableComponent items = Constants.getName(iterator.next());
                              while (iterator.hasNext() && showMessage) {
                                    ItemStack equipped = iterator.next();
                                    items.append(iterator.hasNext() ? Component.literal(", ") : Component.translatable("entity.beansbackpacks.blocked.and"));
                                    items.append(Constants.getName(equipped));
                              }
                              msg = Component.translatable("entity.beansbackpacks.blocked.already_equipped", items).withStyle(ChatFormatting.WHITE);
                        }
                  }
            }

            if (msg != null) {
                  if (showMessage) {
                        this.owner.displayClientMessage(msg, true);
                        if (this.owner.level().isClientSide)
                              Tooltip.pushInventoryMessage(msg);
                  }
                  return false;
            }

            return true;
      }

      public boolean backSlotDisabled() {
            return getDisabling().stream().anyMatch(in -> !in.isEmpty() && !Kind.isWings(in));
      }

      public List<ItemStack> getDisabling() {
            List<ItemStack> items = Services.COMPAT.backSlotDisabled(owner);
            NonNullList<ItemStack> disabling = NonNullList.create();
            for (ItemStack stack : owner.getInventory().armor) {
                  if (!stack.isEmpty() && Constants.elytraOrDisables(stack.getItem()))
                        disabling.add(stack);
            }
            items.forEach(in -> {
                  if (!in.isEmpty())
                        disabling.add(in);
            });
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
                  itemStacks.addAll(getBackpackInventory().getItemStacks());
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

            NonNullList<ItemStack> stacks = this.getBackpackInventory().getItemStacks();
            newBackData.getBackpackInventory().getItemStacks().addAll(stacks);
      }

      public BackpackInventory getBackpackInventory() {
            if (backStack != null && backStack.getItem() instanceof EnderBackpack enderBackpack) {
                  UUID uuid = enderBackpack.getOrCreateUUID(owner, backStack);
                  return EnderStorage.getEnderData(uuid, owner.level());
            }
            return backpackInventory;
      }
}
