package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {
      @Shadow @Final private MinecraftServer server;

      @Inject(method = "placeNewPlayer", at = @At("RETURN"))
      public void playerJoin(Connection ctx, ServerPlayer player, CallbackInfo ci) {
            ServerSave save = ServerSave.getSave(server, false);
            for (UUID uuid : save.heldLockedAdvancement) {
                  if (player.getUUID().equals(uuid)) {
                        Services.REGISTRY.triggerSpecial(player, SpecialCriterion.Special.LOCKED);
                        save.setDirty();
                  }
            }
      }
}
