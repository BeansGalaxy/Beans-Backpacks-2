package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.network.clientbound.SendBackSlot;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

      @Shadow @Final private Entity entity;

      @Inject(method = "addPairing", at = @At("TAIL"))
      public void pairBackData(ServerPlayer owner, CallbackInfo ci) {
            if (entity instanceof ServerPlayer listener) {
                  SendBackSlot.send(owner, listener);
                  SendBackSlot.send(listener, owner);
            }
      }
}
