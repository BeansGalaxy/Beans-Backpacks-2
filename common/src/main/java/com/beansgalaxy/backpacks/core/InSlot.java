package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InSlot extends Slot {
      public static final ResourceLocation BACKPACK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
      public static final ResourceLocation INPUT = new ResourceLocation("sprites/empty_slot_input");
      public static final ResourceLocation INPUT_ALT = new ResourceLocation("sprites/empty_slot_input_alt");
      public final BackData backData;
      public int slotIndex = -1;

      public InSlot(BackData backData, int x, int y) {
            super(backData.backpackInventory, 0, x, y);
            this.backData = backData;
      }

      // RETURN FALSE TO CANCEL A PLAYER'S INVENTORY CLICK
      public static boolean continueSlotClick(int slotIndex, int button, ClickType actionType, Player player) {
            if (slotIndex < 0)
                  return true;

            InventoryMenu inventoryMenu = player.inventoryMenu;
            if (slotIndex > inventoryMenu.slots.size())
                  return true;

            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            BackpackInventory backpackInventory = backData.backpackInventory;
            Inventory playerInventory = player.getInventory();

            ItemStack backpackStack = backpackInventory.getItem(0);
            int maxStack = backpackStack.getMaxStackSize();

            Slot slot = inventoryMenu.slots.get(slotIndex);
            ItemStack stack = slot.getItem();
            ItemStack cursorStack = inventoryMenu.getCarried();

            boolean selectedPlayerInventory = slotIndex < InventoryMenu.SHIELD_SLOT;
            boolean selectedBackpackInventory = slotIndex == backData.inSlot.slotIndex && player.containerMenu == inventoryMenu;

            if (selectedBackpackInventory && Kind.POT.is(backStack))
            {
                  if (actionType == ClickType.THROW && cursorStack.isEmpty())
                  {
                        int count = button == 0 ? 1 : Math.min(stack.getCount(), maxStack);
                        ItemStack itemStack = backpackInventory.removeItem(0, count);
                        player.drop(itemStack, true);
                        return false;
                  }
                  if (actionType == ClickType.SWAP)
                  {
                        ItemStack itemStack = playerInventory.getItem(button);
                        if (itemStack.isEmpty()) {
                              if (backpackStack.getCount() > maxStack)
                              {
                                    playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                    backpackStack.shrink(maxStack);
                                    return false;
                              }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                        }
                        else
                        {
                              if (backpackStack.isEmpty())
                                    return true;
                              if (backpackStack.getCount() > maxStack)
                                    if (playerInventory.add(-2, itemStack))
                                    {
                                          playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                          backpackStack.shrink(maxStack);
                                          return false;
                                    }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                              backpackInventory.insertItem(itemStack);
                        }
                        return false;
                  }
                  if (button == 1 && cursorStack.isEmpty() && backpackStack.getCount() > maxStack)
                  {
                        int count = Math.max(1, maxStack / 2);
                        ItemStack splitStack = backpackInventory.removeItem(0, count);
                        inventoryMenu.setCarried(splitStack);
                        return false;
                  }
            }

            if (actionType == ClickType.THROW)
                  return true;

            if (actionType == ClickType.PICKUP_ALL)
                  return !selectedBackpackInventory;

            if (backData.actionKeyPressed)
            {
                  if (selectedPlayerInventory)
                  {
                        if (backStack.isEmpty() && backData.backSlot.isActive() && !stack.isEmpty() && Kind.isWearable(stack)) {
                              slot.set(backData.backSlot.safeInsert(stack));
                              return false;
                        }
                        if (Kind.isStorage(backStack)) {
                              slot.set(backpackInventory.insertItem(stack));
                              return false;
                        }
                        return true;
                  }
                  if (actionType == ClickType.QUICK_MOVE && stack == backStack)
                        selectedBackpackInventory = true;
                  else if (selectedBackpackInventory)
                        actionType = ClickType.QUICK_MOVE;
            }
            else
                  if (selectedBackpackInventory && actionType != ClickType.QUICK_MOVE)
                  {
                        if (button == 1 && !cursorStack.is(backpackStack.getItem()) && !cursorStack.isEmpty())
                        {
                              backpackInventory.insertItem(cursorStack, 1);
                              return false;
                        }
                        if (button == 0)
                        {
                              inventoryMenu.setRemoteCarried(cursorStack);
                              ItemStack returnStack = backpackInventory.returnItem(0, cursorStack);
                              inventoryMenu.setCarried(returnStack);
                              return false;
                        }
                  }

            if (actionType == ClickType.QUICK_MOVE && selectedBackpackInventory)
            {
                  if (Kind.POT.is(backStack))
                        playerInventory.add(-2, backpackInventory.removeItemNoUpdate(0));
                  else
                        if (playerInventory.getFreeSlot() != -1)
                        {
                              playerInventory.add(-2, backpackStack);
                              if (backpackStack.isEmpty())
                                    backpackInventory.removeItemNoUpdate(0);
                        }
                  return false;
            }

            return true;
      }

      private static void moveAll(BackpackInventory backpackInventory, InventoryMenu inventoryMenu) {
            NonNullList<Slot> slots = inventoryMenu.slots;
            ItemStack cursorStack = inventoryMenu.getCarried();
            int matchingItemsTotalCount = 0;
            for (int j = 9; j < 45; j++) {
                  Slot thisSlot = slots.get(j);
                  ItemStack thisStack = thisSlot.getItem();
                  if (thisStack.is(cursorStack.getItem())) {
                        matchingItemsTotalCount += thisStack.getCount();
                        thisSlot.set(ItemStack.EMPTY);
                  }
            }
            if (matchingItemsTotalCount > 0)
                  backpackInventory.playSound(PlaySound.INSERT);
            while (matchingItemsTotalCount > 0) {
                  int itemsMaxCount = cursorStack.getMaxStackSize();
                  if (matchingItemsTotalCount > itemsMaxCount) {
                        backpackInventory.insertItemSilent(cursorStack.copy(), itemsMaxCount);
                        matchingItemsTotalCount -= itemsMaxCount;
                  } else {
                        backpackInventory.insertItemSilent(cursorStack.copy(), matchingItemsTotalCount);
                        matchingItemsTotalCount = 0;
                  }
            }
      }

      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(BACKPACK_ATLAS, Constants.SLOTS_MOD_ACTIVE ? INPUT_ALT : INPUT);
      }

      @Override
      public boolean isActive() {
            boolean creative = backData.owner.isCreative();
            boolean storage = Kind.isStorage(backData.getStack());
            boolean empty = backData.backpackInventory.isEmpty();
            return !creative && storage || !empty;
      }

      public boolean mayPlace(ItemStack stack) {
            return backData.backpackInventory.canPlaceItem(stack);
      }
}
