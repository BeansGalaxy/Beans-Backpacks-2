package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class UpdateSprintKey {
      @Inject(method = "tick", at = @At("TAIL"))
      public void tick(CallbackInfo ci) {
            LocalPlayer localPlayer = (LocalPlayer) (Object) this;

            long clientWindowHandle = Minecraft.getInstance().getWindow().getWindow();
            String keyName = Tooltip.getKeyBinding().saveString();
            int keyCode = InputConstants.getKey(keyName).getValue();
            boolean actionKeyPressed = InputConstants.isKeyDown(clientWindowHandle, keyCode);
            boolean actionKeyPrevious = BackData.get(localPlayer).actionKeyPressed;

            if (actionKeyPressed != actionKeyPrevious) {
                  BackData.get(localPlayer).actionKeyPressed = actionKeyPressed;
                  Services.NETWORK.SprintKey(actionKeyPressed);
            }
      }

}
