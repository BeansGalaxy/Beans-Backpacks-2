package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface Packet2C {
      Network2C getNetwork();

      default void send2C(ServerPlayer to) {
            Network2C network = getNetwork();
            network.debugMsgEncode();
            Services.NETWORK.send(network, this, to);
      }

      default void send2A(MinecraftServer server) {
            Network2C network = getNetwork();
            network.debugMsgEncode();
            Services.NETWORK.send(network, this, server);
      }

      void encode(FriendlyByteBuf buf);

      void handle();
}
