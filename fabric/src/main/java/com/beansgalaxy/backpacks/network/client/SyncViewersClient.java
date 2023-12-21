package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.SyncViewersPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class SyncViewersClient {
      public static void receiveAtClient(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf byteBuf, PacketSender packetSender) {
            int id = byteBuf.readInt();
            byte viewers = byteBuf.readByte();
            SyncViewersPacket.receiveAtClient(id, viewers);
      }
}
