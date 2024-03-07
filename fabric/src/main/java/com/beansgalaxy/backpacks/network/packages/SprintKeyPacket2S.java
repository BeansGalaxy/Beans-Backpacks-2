package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SprintKeyPacket2S {
      public static void C2S(boolean sprintKeyIsPressed) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(sprintKeyIsPressed);
            ClientPlayNetworking.send(NetworkPackages.SPRINT_KEY_2S, buf);
      }

      public static void receiveAtServer(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler,
                                         FriendlyByteBuf buf, PacketSender responseSender) {
            boolean sprintKeyPressed = buf.readBoolean();
            BackData.get(serverPlayer).actionKeyPressed = sprintKeyPressed;
      }
}
