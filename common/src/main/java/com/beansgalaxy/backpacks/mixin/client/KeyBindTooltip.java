package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.mojang.datafixers.kinds.Const;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyBindsList.KeyEntry.class)
public abstract class KeyBindTooltip {
      @Shadow @Final private Button changeButton;
      @Shadow @Final private Component name;

      @Inject(method = "refreshEntry", at = @At(value = "INVOKE", ordinal = 1, shift = At.Shift.AFTER,
                  target = "Lnet/minecraft/client/gui/components/Button;setTooltip(Lnet/minecraft/client/gui/components/Tooltip;)V"))
      private void changeBackpackKeyHover(CallbackInfo ci) {
            if (name.equals(Component.translatable(KeyPress.ACTION_KEY_IDENTIFIER))) {
                  String key = Constants.CLIENT_CONFIG.sneak_default.get() ? "key.sneak" : "key.sprint";
                  this.changeButton.setTooltip(Tooltip.create(Component.translatable(KeyPress.ACTION_KEY_DESC, Component.translatable(key))));
            } else if (name.equals(Component.translatable(KeyPress.MENUS_KEY_IDENTIFIER))) {
                  this.changeButton.setTooltip(Tooltip.create(Component.translatable(KeyPress.MENUS_KEY_DESC, Component.translatable("key.beansbackpacks.action"))));
            }
      }
}
