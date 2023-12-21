package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

public class SyncViewersS2All {
      public static void S2C(Entity owner, byte viewers) {
            Level world = owner.level();
            List<ServerPlayer> playerList = world.getServer().getPlayerList().getPlayers();
            for (ServerPlayer serverPlayer : playerList)
                  if (serverPlayer.level().dimension() == world.dimension()) {
                        FriendlyByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(owner.getId());
                        buf.writeByte(viewers);

                        ServerPlayNetworking.send(serverPlayer, NetworkPackages.SYNC_VIEWERS_2All, buf);
                  }
      }
}
