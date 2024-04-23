package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.WrapCreativeSlot;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.serverbound.ClearBackSlot;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.InSlot;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = CreativeModeInventoryScreen.class, priority = 899)
public abstract class CreativeInventoryMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
      public CreativeInventoryMixin(CreativeModeInventoryScreen.ItemPickerMenu p_98701_, Inventory p_98702_, Component p_98703_) {
            super(p_98701_, p_98702_, p_98703_);
      }

      @Inject(method = "selectTab", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "FIELD",
                  shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;destroyItemSlot:Lnet/minecraft/world/inventory/Slot;"))
      private void addBackSlot(CreativeModeTab creativeModeTab, CallbackInfo ci, CreativeModeTab creativeModeTab2) {
            BackData backData = BackData.get(minecraft.player);
            if (Constants.SLOTS_MOD_ACTIVE)
                  menu.slots.add(new WrapCreativeSlot(backData.inSlot));
            else {
                  BackSlot backSlot = backData.backSlot;
                  menu.slots.set(backSlot.slotIndex, new WrapCreativeSlot(backSlot));
            }
      }

      @Inject(method = "slotClicked", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/core/NonNullList;size()I"))
      private void clearBackSlot(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
            ClearBackSlot.send(BackData.get(minecraft.player));
      }
}
