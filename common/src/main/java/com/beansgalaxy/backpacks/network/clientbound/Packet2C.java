package com.beansgalaxy.backpacks.network.clientbound;

import net.minecraft.network.FriendlyByteBuf;

public interface Packet2C {
      void encode(FriendlyByteBuf buf);

      void handle();
}
