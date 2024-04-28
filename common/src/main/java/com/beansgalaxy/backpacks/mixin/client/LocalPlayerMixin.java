package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.ClickAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import com.beansgalaxy.backpacks.network.serverbound.ActionKey;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
      @Inject(method = "tick", at = @At("TAIL"))
      public void tick(CallbackInfo ci) {
            SyncBackSlot.indexHeldSlots();

            LocalPlayer localPlayer = (LocalPlayer) (Object) this;
            Minecraft instance = Minecraft.getInstance();
            KeyMapping keyBinding = Tooltip.getKeyBinding();

            KeyMapping sneakKey = instance.options.keyShift;
            if (sneakKey.same(keyBinding))
                  sneakKey.setDown(keyBinding.isDown());

            InputConstants.Key key = InputConstants.getKey(keyBinding.saveString());
            boolean isMouseKey = key.getType().equals(InputConstants.Type.MOUSE);


            long window = instance.getWindow().getWindow();
            int value = key.getValue();

            BackData backData = BackData.get(localPlayer);
            boolean actionKeyPressed = isMouseKey ? GLFW.glfwGetMouseButton(window, value) == 1 : InputConstants.isKeyDown(window, value);
            boolean actionKeyPrevious = backData.actionKeyPressed;

            if (actionKeyPressed == actionKeyPrevious)
                  return;

            backData.actionKeyPressed = actionKeyPressed;
            ActionKey.send(actionKeyPressed);

            boolean instantPlace = Constants.CLIENT_CONFIG.instant_place.get();
            if ((instantPlace || isMouseKey) && actionKeyPressed) {
                  if (instance.screen instanceof ClickAccessor clickAccessor)
                        clickAccessor.beans_Backpacks_2$instantPlace();
                  else if (!(instance.screen instanceof BackpackScreen))
                        KeyPress.instantPlace(localPlayer);
            }
      }

      @Inject(method = "handleEntityEvent", at = @At("HEAD"))
      private void catchBackSlotEvent(byte id, CallbackInfo ci) {

      }

}
