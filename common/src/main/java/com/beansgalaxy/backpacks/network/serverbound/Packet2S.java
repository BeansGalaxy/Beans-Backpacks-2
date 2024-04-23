package com.beansgalaxy.backpacks.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface Packet2S {
      void encode(FriendlyByteBuf buf);

      void handle(ServerPlayer sender);
}
