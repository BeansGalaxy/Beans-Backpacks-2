package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
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
            Minecraft instance = Minecraft.getInstance();

            KeyMapping sprintKey = instance.options.keySprint;
            KeyMapping customKey = KeyPress.INSTANCE.ACTION_KEY;
            boolean isCustomUnbound = customKey.same(sprintKey) || customKey.isUnbound();

            KeyMapping actionKey = isCustomUnbound ? sprintKey : customKey;

            long clientWindowHandle = instance.getWindow().getWindow();
            String keyName = actionKey.saveString();
            int keyCode = InputConstants.getKey(keyName).getValue();
            boolean actionKeyPressed = InputConstants.isKeyDown(clientWindowHandle, keyCode);
            boolean actionKeyPrevious = BackSlot.get(localPlayer).actionKeyPressed;

            if (actionKeyPressed != actionKeyPrevious) {
                  BackSlot.get(localPlayer).actionKeyPressed = actionKeyPressed;
                  Services.NETWORK.SprintKey(actionKeyPressed);
            }
      }

}
