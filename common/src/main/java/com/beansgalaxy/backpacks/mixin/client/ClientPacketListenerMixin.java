package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
      @Shadow @Final private Minecraft minecraft;

      @Inject(method = "handleLogin", at = @At("TAIL"))
      private void test(ClientboundLoginPacket ctx, CallbackInfo ci) {
            CommonAtClient.sendBackpackCapePos(minecraft.player);
      }
}
