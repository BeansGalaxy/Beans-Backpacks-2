package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.entity.BackpackMenu;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryMenu.class, priority = 899)
public abstract class InventoryMenuMixin extends RecipeBookMenu<TransientCraftingContainer>{

      @Shadow @Final private Player owner;
      @Shadow @Final private static EquipmentSlot[] SLOT_IDS;

      @Shadow public abstract ItemStack quickMoveStack(Player player, int i);

      public InventoryMenuMixin(MenuType<?> $$0, int $$1) {
            super($$0, $$1);
      }

      @Inject(method = "<init>", at = @At("TAIL"))
      private void onConstructed(Inventory playerInventory, boolean isServerSide, Player player, CallbackInfo ci) {
            BackData backData = ((BackAccessor) playerInventory).getBackData();
            if (!Services.COMPAT.anyModsLoaded(new String[]{CompatHelper.CURIOS, CompatHelper.TRINKETS}))
            {
                  backData.backSlot.slotIndex = slots.size();
                  this.addSlot(backData.backSlot);
            } else
            {
                  backData.inSlot.slotIndex = slots.size();
                  this.addSlot(backData.inSlot);
            }
      }

      @Inject(method = "quickMoveStack", cancellable = true, at = @At("HEAD"))
      private void quickMove(Player player, int slotInt, CallbackInfoReturnable<ItemStack> cir) {
            BackData backData = ((BackAccessor) player.getInventory()).getBackData();
            Slot slot = this.slots.get(slotInt);
            ItemStack stack = slot.getItem();
            if ((Kind.isWearable(stack)) && backData.isEmpty() && !backData.backSlotDisabled()) {
                  backData.set(stack);
                  slot.set(ItemStack.EMPTY);
                  cir.setReturnValue(ItemStack.EMPTY);
            }
      }

      @Unique
      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            if (slotIndex < 0 || slots.size() < slotIndex || player.isCreative()) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            BackpackInventory backpackInventory = backData.backpackInventory;
            Inventory playerInventory = player.getInventory();
            Slot slot = slots.get(slotIndex);
            ItemStack stack = slot.getItem();

            boolean selectedPlayerInventory = slotIndex < InventoryMenu.SHIELD_SLOT && slotIndex > 8;
            boolean selectedBackpackInventory = slotIndex == backData.inSlot.slotIndex && player.containerMenu == player.inventoryMenu;

            boolean selectedEquipment = !selectedPlayerInventory && slotIndex > 4 && !selectedBackpackInventory;

            ItemStack cursorStack = getCarried();
            if (selectedEquipment && !slot.hasItem() && !cursorStack.isEmpty() && !backStack.isEmpty()
                        && (Constants.DISABLES_BACK_SLOT.contains(cursorStack.getItem()) || Constants.ELYTRA_ITEMS.contains(cursorStack.getItem())))
            {
                  if (player.level().isClientSide)
                        Tooltip.playSound(Kind.fromStack(backStack), PlaySound.HIT);
                  return;
            }

            if (actionType == ClickType.QUICK_MOVE && !selectedBackpackInventory && !backData.isEmpty()) {
                  if (Constants.DISABLES_BACK_SLOT.contains(stack.getItem()) || Constants.ELYTRA_ITEMS.contains(stack.getItem())) {
                        if (player.level().isClientSide())
                              Tooltip.playSound(Kind.fromStack(backData.getStack()), PlaySound.HIT);
                        return;
                  }
            }


            if (slotIndex < InventoryMenu.INV_SLOT_START) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (selectedBackpackInventory && Kind.POT.is(backStack) && beans_Backpacks_2$potClick(slotIndex, button, actionType, stack, backpackInventory, playerInventory))
                  return;

            if (actionType == ClickType.THROW) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (actionType == ClickType.PICKUP_ALL) {
                  if (!selectedBackpackInventory)
                        super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (backData.actionKeyPressed) {
                  if (selectedPlayerInventory) {
                        if (backStack.isEmpty() && backData.backSlot.isActive() && !stack.isEmpty() && Kind.isWearable(stack))
                              slot.set(backData.backSlot.safeInsert(stack));
                        else if (Kind.isStorage(backStack))
                              slot.set(backpackInventory.insertItem(stack, stack.getCount()));
                        else
                              super.clicked(slotIndex, button, actionType, player);
                        return;
                  }
                  if (selectedBackpackInventory || actionType == ClickType.QUICK_MOVE && stack == backStack) {
                        actionType = ClickType.QUICK_MOVE;
                        selectedBackpackInventory = true;
                  }
            }
            else if (selectedBackpackInventory && actionType != ClickType.QUICK_MOVE) {
                  setCarried(BackpackMenu.menuInsert(button, cursorStack, 0, backpackInventory));
                  return;
            }

            if (actionType == ClickType.QUICK_MOVE && selectedBackpackInventory) {
                  BackpackItem.handleQuickMove(playerInventory, backpackInventory);
                  return;
            }

            super.clicked(slotIndex, button, actionType, player);
      }

      @Unique
      private boolean beans_Backpacks_2$potClick(int slotIndex, int button, ClickType actionType, ItemStack stack,
                              BackpackInventory backpackInventory, Inventory playerInventory) {
            ItemStack cursorStack = getCarried();
            ItemStack backpackStack = backpackInventory.getItem(0);
            int maxStack = backpackStack.getMaxStackSize();
            if (actionType == ClickType.THROW && cursorStack.isEmpty())
            {
                  int count = button == 0 ? 1 : Math.min(stack.getCount(), maxStack);
                  ItemStack itemStack = backpackInventory.removeItem(0, count);
                  owner.drop(itemStack, true);
                  return true;
            }
            if (actionType == ClickType.SWAP)
            {
                  ItemStack itemStack = playerInventory.getItem(button);
                  if (itemStack.isEmpty()) {
                        if (backpackStack.getCount() > maxStack)
                        {
                              playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                              backpackStack.shrink(maxStack);
                              return true;
                        }
                        playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                  }
                  else
                  {
                        if (backpackStack.isEmpty()) {
                              super.clicked(slotIndex, button, actionType, owner);
                              return true;
                        }
                        if (backpackStack.getCount() > maxStack)
                              if (playerInventory.add(-2, itemStack))
                              {
                                    playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                    backpackStack.shrink(maxStack);
                                    return true;
                              }
                        playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                        backpackInventory.insertItem(itemStack, itemStack.getCount());
                  }
                  return true;
            }
            if (button == 1 && cursorStack.isEmpty() && backpackStack.getCount() > maxStack)
            {
                  int count = Math.max(1, maxStack / 2);
                  ItemStack splitStack = backpackInventory.removeItem(0, count);
                  setCarried(splitStack);
                  return true;
            }
            return false;
      }
}
