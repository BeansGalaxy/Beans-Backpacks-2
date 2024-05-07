package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.ClickAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.clientbound.SendBackInventory;
import com.beansgalaxy.backpacks.network.clientbound.SendBackSlot;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderSound;
import com.beansgalaxy.backpacks.network.serverbound.SyncActionKey;
import com.beansgalaxy.backpacks.events.KeyPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
      @Shadow @Final protected Minecraft minecraft;
      @Unique private LocalPlayer instance = (LocalPlayer) (Object) this;

      @Inject(method = "tick", at = @At("TAIL"))
      public void tick(CallbackInfo ci) {
            SendBackSlot.indexHeldSlots();
            SendBackInventory.indexInventories();
            SendEnderSound.indexSounds(instance);

            KeyPress.isPressed actionKey = KeyPress.isPressed(minecraft, KeyPress.getActionKeyBind());
            KeyPress.isPressed menusKey = KeyPress.isPressed(minecraft, KeyPress.getMenusKeyBind());

            BackData backData = BackData.get(instance);
            if (actionKey.pressed() == backData.actionKeyDown && menusKey.pressed() == backData.menusKeyDown)
                  return;

            backData.actionKeyDown = actionKey.pressed();
            backData.menusKeyDown = menusKey.pressed();
            SyncActionKey.send(actionKey.pressed(), menusKey.pressed());

            boolean instantPlace = Constants.CLIENT_CONFIG.instant_place.get();
            if (actionKey.pressed() && (instantPlace || actionKey.onMouse()) && minecraft.screen == null) {
                  KeyPress.instantPlace(instance);
            }
            else if (menusKey.pressed() && menusKey.onMouse() && minecraft.screen instanceof ClickAccessor clickAccessor)
                  clickAccessor.beans_Backpacks_2$instantPlace();

            System.out.println("A:" + actionKey.pressed() + "   M:" + menusKey.pressed());
      }

}
