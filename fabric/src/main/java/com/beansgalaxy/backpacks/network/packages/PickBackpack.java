package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.events.PickBlockEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PickBackpack {
      public static void receiveAtServer(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler,
                                         FriendlyByteBuf buf, PacketSender responseSender) {
            PickBlockEvent.pickBackpack(buf.readInt(), serverPlayer);
      }
}
