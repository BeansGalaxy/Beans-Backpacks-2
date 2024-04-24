package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface Packet2S {
      Network2S getNetwork();

      default void send2S() {
            Network2S network = getNetwork();
            network.debugMsgEncode();
            Services.NETWORK.send(network, this);
      }

      void encode(FriendlyByteBuf buf);

      void handle(ServerPlayer sender);
}
