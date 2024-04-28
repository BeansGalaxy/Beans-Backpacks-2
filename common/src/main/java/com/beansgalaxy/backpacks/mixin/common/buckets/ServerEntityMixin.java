package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
      @Inject(method = "addPairing", at = @At("TAIL"))
      public void pairBackData(ServerPlayer owner, CallbackInfo ci) {
            List<? extends Player> players = owner.level().players();
            for (Player listener : players) {
                  if (listener.equals(owner))
                        SyncBackSlot.send(owner, owner);
                  else {
                        SyncBackSlot.send(owner, (ServerPlayer) listener);
                        SyncBackSlot.send(listener, owner);
                  }
            }
      }
}
