package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BackpackMenu extends AbstractContainerMenu {
      public static int FIRST_SLOT_INDEX;
      public final BackpackInventory backpackInventory;
      protected final Backpack mirror;
      protected final Player viewer;
      protected final Entity owner;
      protected final BlockPos ownerPos;
      protected final float ownerYaw;
      private final int max_stacks;
      public int invOffset = 108;

      // SERVER CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInventory, BackpackInventory backpackInventory) {
            super(Services.REGISTRY.getMenu(), id);
            this.backpackInventory = backpackInventory;
            this.owner = backpackInventory.getOwner();
            this.ownerPos = owner.blockPosition();
            this.ownerYaw = owner.getVisualRotationYInDegrees();
            this.viewer = playerInventory.player;
            this.mirror = new Backpack(owner.level()) {
                  @Override
                  public UUID getPlacedBy() {
                        return backpackInventory.getPlacedBy();
                  }

                  @Override
                  public CompoundTag getTrim() {
                        Traits.LocalData localData = getLocalData();
                        if (Kind.ENDER.is(localData.kind()))
                              return EnderStorage.getTrim(getPlacedBy(), level());

                        return localData.trim;
                  }

                  @Override public BackpackInventory.Viewable getViewable() {
                        return backpackInventory.getViewable();
                  }

                  @Override public Traits.LocalData getLocalData() {
                        return backpackInventory.getLocalData();
                  }
            };
            this.max_stacks = backpackInventory.getLocalData().maxStacks();
            createInventorySlots(playerInventory);
            FIRST_SLOT_INDEX = slots.size();
            createBackpackSlots(backpackInventory);

            if (owner instanceof EntityEnder ender && viewer instanceof ServerPlayer serverPlayer) {
                  EnderStorage.updateLocations(ender.getPlacedBy(), serverPlayer.serverLevel());
            }
      }

      @Override
      public void broadcastChanges() {
            super.broadcastChanges();
      }

      @Override
      public void slotsChanged(Container $$0) {
            super.slotsChanged($$0);
      }

      // CLIENT CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
            this(id, playerInv, getBackpackInventory(buf.readInt(), playerInv.player.level()));
      }

      public static BackpackInventory getBackpackInventory(int entityId, Level level) {
            Entity entity = level.getEntity(entityId);
            return BackpackInventory.get(entity);
      }


      private void createBackpackSlots(Container inventory) {
            final int columns = Math.min(4 + (max_stacks / 3), 11);
            final int rows = 4;
            final int spacing = 17;
            int bpCenter = (columns / 2) * spacing;
            int x = 80 - bpCenter;
            x += spacing / 2 * -((columns % 2) - 1);
            int y = invOffset - rows * spacing + 35;

            for(int r = 0; r < rows; ++r)
                  for(int c = 0; c < columns; ++c)
                        this.addSlot(new Slot(inventory, c + r * columns, x + c * spacing, y + r * spacing) {
                              public boolean mayPlace(ItemStack p_40231_) {
                                    return false;
                              }
                        });
      }

      private void createInventorySlots(Inventory playerInventory) {
            for(int l = 0; l < 3; ++l) {
                  for(int k = 0; k < 9; ++k) {
                        this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51 + invOffset));
                  }
            }
            for(int i1 = 0; i1 < 9; ++i1) {
                  this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 109 + invOffset));
            }
      }

      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            Kind kind = backpackInventory.getLocalData().kind();
            if (owner.level().isClientSide() && Kind.ENDER.is(kind))
                  return;

            int backpackSlot = slotIndex - FIRST_SLOT_INDEX;
            if (actionType == ClickType.THROW) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (actionType == ClickType.PICKUP_ALL) {
                  if (!(backpackSlot > -1))
                        super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (BackData.get(player).actionKeyPressed)
                  actionType = ClickType.QUICK_MOVE;


            if (actionType != ClickType.QUICK_MOVE && backpackSlot > -1) {
                  ItemStack cursorStack = this.getCarried();
                  setCarried(menuInsert(button, cursorStack, backpackSlot, backpackInventory));
                  return;
            }
            super.clicked(slotIndex, button, actionType, player);
      }

      public static ItemStack menuInsert(int button, ItemStack cursorStack, int slot, BackpackInventory backpackInventory) {
            if (button == 1) {
                  if (cursorStack.isEmpty()) {
                        ItemStack stack = backpackInventory.getItem(slot);
                        int count = Math.max(1, Math.min(stack.getCount(), stack.getMaxStackSize()) / 2);
                        return backpackInventory.removeItem(slot, count);
                  } else
                        return backpackInventory.returnItem(slot, cursorStack, 1);
            } else
                  return backpackInventory.returnItem(slot, cursorStack);
      }

      @Override
      public ItemStack quickMoveStack(Player player, int slotId) {
            Slot clickedSlot = slots.get(slotId);
            ItemStack clickedStack = clickedSlot.getItem();
            if (clickedStack == ItemStack.EMPTY)
                  return ItemStack.EMPTY;
            if (slotId < FIRST_SLOT_INDEX) { // HANDLES INSERT TO BACKPACK
                  if (backpackInventory.spaceLeft() < 1)
                        return ItemStack.EMPTY;
                  backpackInventory.insertItem(clickedStack, clickedStack.getCount());
                  clickedSlot.set(clickedStack);
            } else { // HANDLES INSERT TO INVENTORY
                  clickedStack = backpackInventory.getItemStacks().get(slotId - FIRST_SLOT_INDEX);
                  this.moveItemStackTo(clickedStack, 0, FIRST_SLOT_INDEX, true);
                  if (clickedStack.isEmpty()) {
                        backpackInventory.removeItemNoUpdate(slotId - FIRST_SLOT_INDEX);
                        backpackInventory.playSound(PlaySound.TAKE);
                  }
            }
            return ItemStack.EMPTY;
      }

      @Override
      public boolean stillValid(Player player) {
            return true;
      }

      public void removed(Player player) {
            backpackInventory.stopOpen(player);
            mirror.discard();
            super.removed(player);
      }
}
