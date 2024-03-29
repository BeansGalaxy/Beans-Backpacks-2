package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.inventory.PotInventory;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryMenu.class, priority = 899)
public abstract class InventoryMenuMixin extends RecipeBookMenu<TransientCraftingContainer>{

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
                  this.addSlot(backData.inSlot);

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
                  backData.playEquipSound(stack);
            }
      }

      @Unique boolean cancelQuickMoveArmor = false;

      @Redirect(method = "quickMoveStack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EquipmentSlot$Type;ARMOR:Lnet/minecraft/world/entity/EquipmentSlot$Type;"))
      private EquipmentSlot.Type cancelArmorEquip() {
            return cancelQuickMoveArmor ? null : EquipmentSlot.Type.ARMOR;
      }

      @Unique
      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            if (slotIndex < 0 || slots.size() < slotIndex) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            Traits.LocalData traits = backData.getTraits();
            Kind kind = traits.kind;
            BackpackInventory backpackInventory = backData.backpackInventory;
            Slot slot = slots.get(slotIndex);
            ItemStack stack = slot.getItem();

            boolean selectedBackSlot = slot instanceof BackSlot;
            boolean selectedPlayerInventory = slotIndex < InventoryMenu.SHIELD_SLOT + 1 && slotIndex > 8;
            boolean selectedBackpackInventory = (stack == backStack && !stack.isEmpty()) || slot instanceof InSlot;
            boolean selectedEquipment = !selectedPlayerInventory && slotIndex > 4 && !selectedBackpackInventory;

            ItemStack cursorStack = getCarried();
            if (selectedEquipment && !selectedBackSlot && Constants.CHESTPLATE_DISABLED.contains(cursorStack.getItem())) {
                  return;
            }

            if (!backData.isEmpty() && actionType == ClickType.QUICK_MOVE && Constants.canEquipWithBackpack(stack.getItem())) {
                  cancelQuickMoveArmor = true;
                  super.clicked(slotIndex, button, actionType, player);
                  cancelQuickMoveArmor = false;
                  return;
            }

            Level level = player.level();
            if (!backData.isEmpty() && Constants.elytraOrDisables(cursorStack.getItem()))
            {
                  if (selectedEquipment && !slot.hasItem() && !cursorStack.isEmpty())
                  {
                        if (level.isClientSide)
                              Tooltip.playSound(kind, PlaySound.HIT);
                        return;
                  }
                  if (actionType == ClickType.QUICK_MOVE && !selectedBackpackInventory) {
                        if (level.isClientSide())
                              Tooltip.playSound(kind, PlaySound.HIT);
                        return;
                  }
            }

            if (slotIndex < InventoryMenu.INV_SLOT_START || actionType == ClickType.THROW) {
                  super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (actionType == ClickType.PICKUP_ALL) {
                  if (!selectedBackpackInventory)
                        super.clicked(slotIndex, button, actionType, player);
                  return;
            }

            if (selectedBackpackInventory) {
                  if (actionType == ClickType.SWAP)
                        return;

                  ClickAction clickAction = button == 1 ? ClickAction.SECONDARY : ClickAction.PRIMARY;
                  SlotAccess slotAccess = new SlotAccess() {
                        public ItemStack get() {
                              return getCarried();
                        }

                        public boolean set(ItemStack stack) {
                              setCarried(stack);
                              return true;
                        }
                  };
                  if (BackpackItem.interact(backStack, clickAction, player, slotAccess, actionType == ClickType.QUICK_MOVE)) {
                        if (player instanceof ServerPlayer serverPlayer && Kind.ENDER.is(kind))
                              Services.NETWORK.sendEnderData2C(serverPlayer, serverPlayer.getUUID());
                        return;
                  }
            }

            if (backData.actionKeyPressed && selectedPlayerInventory ) {
                  if (backStack.isEmpty() && backData.backSlot.isActive() && !stack.isEmpty() && Kind.isWearable(stack)) {
                        slot.set(backData.backSlot.safeInsert(stack));
                        return;
                  }
                  else if (Kind.POT.is(kind)) {
                        PotInventory.add(backStack, stack, player);
                        return;
                  }
                  else if (Kind.CAULDRON.is(kind)) {
                        ItemStack returned = CauldronInventory.quickInsert(backStack, stack, level);
                        if (!returned.isEmpty()) {
                              if (stack.isEmpty())
                                    slot.set(returned);
                              else
                                    player.getInventory().placeItemBackInInventory(returned);
                        }
                        return;
                  }
                  else if (traits.isStorage()) {
                        if (!level.isClientSide() || !Kind.ENDER.is(kind))
                              slot.set(backpackInventory.insertItem(stack, stack.getCount()));
                        if (player instanceof ServerPlayer serverPlayer && Kind.ENDER.is(kind))
                              Services.NETWORK.sendEnderData2C(serverPlayer, serverPlayer.getUUID());
                        return;
                  }
            }

            super.clicked(slotIndex, button, actionType, player);
      }
}
