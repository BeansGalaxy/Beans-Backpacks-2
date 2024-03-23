package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.WrapCreativeSlot;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.InSlot;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
      public CreativeInventoryMixin(CreativeModeInventoryScreen.ItemPickerMenu p_98701_, Inventory p_98702_, Component p_98703_) {
            super(p_98701_, p_98702_, p_98703_);
      }

      @Inject(method = "selectTab", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
                  shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/core/NonNullList;add(Ljava/lang/Object;)Z"))
      private void addBackSlot(CreativeModeTab p_98561_, CallbackInfo ci, CreativeModeTab creativemodetab, AbstractContainerMenu abstractcontainermenu, int k, int l, int i1, Slot slot) {
            Slot targetSlot = abstractcontainermenu.slots.get(k);
            if (targetSlot instanceof BackSlot || targetSlot instanceof InSlot) {
                  menu.slots.set(k, new WrapCreativeSlot(targetSlot));
            }
      }
}
