package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.clientbound.SendBackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.level.entity.EntityAccess;

import java.util.UUID;

public class BackpackMenu extends AbstractContainerMenu {
      public static int FIRST_SLOT_INDEX;
      public final BackpackInventory backpackInventory;
      protected final Backpack mirror;
      protected final Player viewer;
      protected final EntityAccess owner;
      protected final NonNullList<MenuSlot> backpackSlots = NonNullList.create();
      public int invOffset = 108;

      // SERVER CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInventory, BackpackInventory backpackInventory) {
            super(Services.REGISTRY.getMenu(), id);
            this.backpackInventory = backpackInventory;
            this.owner = backpackInventory.getOwner();
            this.viewer = playerInventory.player;
            this.mirror = new Backpack(viewer.level()) {

                  @Override
                  public UUID getPlacedBy() {
                        return backpackInventory.getPlacedBy();
                  }

                  @Override public Traits.LocalData getTraits() {
                        return backpackInventory.getTraits();
                  }

                  @Override
                  public Viewable getViewable() {
                        return backpackInventory.getViewable();
                  }
            };

            createInventorySlots(playerInventory);
            createBackpackSlots(backpackInventory);
            FIRST_SLOT_INDEX = slots.size();

            if (owner instanceof EntityEnder ender && viewer instanceof ServerPlayer serverPlayer) {
                  EnderStorage.Location.update(ender.getPlacedBy(), serverPlayer.serverLevel());
            }
      }

      @Override
      public void broadcastChanges() {
            if (owner instanceof ServerPlayer serverPlayer)
                  SendBackInventory.send(serverPlayer);
            if (!backpackInventory.getTraits().kind.is(Kind.ENDER)) 
                  SendBackInventory.send(this);

      }

      @Override
      public void slotsChanged(Container $$0) {
            super.slotsChanged($$0);
      }

      // CLIENT CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
            this(id, playerInv, getBackpackInventory(buf, playerInv.player.level()));
      }

      public static BackpackInventory getBackpackInventory(FriendlyByteBuf buf, Level level) {
            int id = buf.readInt();
            if (id == -1) {
                  UUID uuid = buf.readUUID();
                  return EnderStorage.getEnderData(uuid, level);
            }

            Entity entity = level.getEntity(id);
            return BackpackInventory.get(entity);
      }

      private void createBackpackSlots(BackpackInventory inventory) {
            for (int i = 0; i < MenuSlot.MAX_SLOTS + 1; i++)
                  backpackSlots.add(new MenuSlot(this, i, this::updateSlots));

            for (Slot backpackSlot : backpackSlots) {
                  addSlot(backpackSlot);
            }

            updateSlots();
      }

      public void updateSlots() {
            boolean hasSpace = backpackInventory.spaceLeft() > 0;
            int size = backpackInventory.getContainerSize();
            int shift = Math.max(0, size - MenuSlot.MAX_SLOTS - (hasSpace? 0: 1));
            for (MenuSlot backpackSlot : backpackSlots) {
                  int backIndex = backpackSlot.backIndex;
                  if (backIndex + shift < size) {
                        backpackSlot.index = backIndex + 36;
                        backpackSlot.state = MenuSlot.State.ACTIVE;
                        int[] xy = MenuSlot.getXY(backpackInventory, backIndex, hasSpace);
                        backpackSlot.x = xy[0];
                        backpackSlot.y = xy[1];
                        continue;
                  }
                  if (hasSpace && backIndex + shift == size) {
                        backpackSlot.index = size + 36;
                        backpackSlot.state = MenuSlot.State.EMPTY;
                        int[] xy = MenuSlot.getXY(backpackInventory, -1, true);
                        backpackSlot.x = xy[0];
                        backpackSlot.y = xy[1];
                  } else {
                        backpackSlot.index = backIndex + 36;
                        backpackSlot.state = MenuSlot.State.HIDDEN;
                        backpackSlot.x = 0;
                        backpackSlot.y = 0;
                  }
            }
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

      @Override
      public boolean canDragTo(Slot slot) {
            return !(slot instanceof MenuSlot);
      }

      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            handleClick(slotIndex, button, actionType, player);
            updateSlots();
      }

      private void handleClick(int slotIndex, int button, ClickType actionType, Player player) {
            ItemStack carried = getCarried();
            boolean carriedEmpty = carried.isEmpty();
            if (slotIndex >= slots.size()) {
                  if (carriedEmpty)
                        return;
                  else
                        slotIndex = slots.size() - 1;
            }

            int backIndex = -1;
            if (slotIndex > 0 && getSlot(slotIndex) instanceof MenuSlot menuSlot) {
                  if (carriedEmpty && menuSlot.getItem().isEmpty()) return;
                  backIndex = menuSlot.backIndex;
            }

            if (actionType == ClickType.THROW) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (actionType == ClickType.PICKUP_ALL) {
                  if (backIndex == -1)
                        super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (BackData.get(player).menusKeyDown)
                  actionType = ClickType.QUICK_MOVE;


            if (backIndex != -1) {
                  if (actionType == ClickType.QUICK_MOVE)
                        BackpackItem.handleQuickMove(player.getInventory(), backpackInventory, backIndex);
                  else {
                        if (backIndex >= backpackInventory.getItemStacks().size()) backIndex = 0;
                        else if (!carriedEmpty) backIndex += 1;
                        setCarried(menuInsert(button, carried, backIndex, backpackInventory));
                  }

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
                  backpackInventory.insertItem(clickedStack, clickedStack.getCount(), 0);
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
