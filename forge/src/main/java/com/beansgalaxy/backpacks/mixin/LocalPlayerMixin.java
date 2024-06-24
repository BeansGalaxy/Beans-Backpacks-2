package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
      public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
            super(p_250460_, p_249912_);
      }

      @Inject(method = "aiStep", cancellable = true, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public void getItemBySlot(CallbackInfo ci) {
            LocalPlayer instance = (LocalPlayer) (Object) this;
            ItemStack stack = BackData.get(instance).getStack();
            if (Kind.isWings(stack) && ElytraItem.isFlyEnabled(stack) && this.tryToStartFallFlying()) {
                  this.setSharedFlag(7, true);
                  instance.connection.send(new ServerboundPlayerCommandPacket(instance, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                  ci.cancel();
            }
      }

}
