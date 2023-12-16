package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
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
            int sprintKeyCode = Minecraft.getInstance().options.keySprint.getDefaultKey().getValue();
            boolean sprintKeyPressed = InputConstants.isKeyDown(clientWindowHandle, sprintKeyCode);
            boolean sprintKeyPrevious = BackSlot.get(localPlayer).sprintKeyIsPressed;

            if (sprintKeyPressed != sprintKeyPrevious) {
                  BackSlot.get(localPlayer).sprintKeyIsPressed = sprintKeyPressed;
                  Services.NETWORK.SprintKey(sprintKeyPressed);
            }
      }

}
