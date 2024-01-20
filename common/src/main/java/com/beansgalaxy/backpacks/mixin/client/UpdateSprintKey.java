package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
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
            KeyMapping keyBinding = Tooltip.getKeyBinding();

            KeyMapping sneakKey = instance.options.keyShift;
            if (sneakKey.same(keyBinding))
                  sneakKey.setDown(keyBinding.isDown());

            InputConstants.Key key = InputConstants.getKey(keyBinding.saveString());

            BackData backData = BackData.get(localPlayer);
            boolean actionKeyPressed = InputConstants.isKeyDown(instance.getWindow().getWindow(), key.getValue());
            boolean actionKeyPrevious = backData.actionKeyPressed;

            if (actionKeyPressed != actionKeyPrevious) {
                  backData.actionKeyPressed = actionKeyPressed;
                  Services.NETWORK.SprintKey(actionKeyPressed);
            }
      }

}
