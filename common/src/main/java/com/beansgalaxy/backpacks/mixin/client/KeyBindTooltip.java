package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.events.KeyPress;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyBindTooltip {
      @Shadow @Final private Button changeButton;
      @Shadow @Final private Component name;

      @Redirect(method = "refreshEntry", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/components/Button;setTooltip(Lnet/minecraft/client/gui/components/Tooltip;)V"))
      private void redirect(Button instance, Tooltip tooltip) {
            if (name.equals(Component.translatable(KeyPress.KEY_BACKPACK_MODIFIER))) {
                  tooltip = Tooltip.create(Component.translatable(KeyPress.KEY_DESCRIPTION));
            }
            this.changeButton.setTooltip(tooltip);
      }

}
