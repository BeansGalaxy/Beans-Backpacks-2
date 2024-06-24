package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.network.clientbound.Packet2C;
import com.beansgalaxy.backpacks.network.serverbound.Packet2S;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class NetworkPackages {
      public static void register2S() {
            for (Network2S value : Network2S.values())
                  ServerPlayNetworking.registerGlobalReceiver(value.id, ((server, player, handler, buf, responseSender) -> {
                        value.packet.decoder().apply(buf).handle(player);
                  }));
      }

      public static void register2C() {
            for (Network2C value : Network2C.values())
                  ClientPlayNetworking.registerGlobalReceiver(value.id, (minecraft, handler, buf, packetSender) -> {
                        value.packet.decoder().apply(buf).handle();
                  });
      }
}
