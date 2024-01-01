package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.client.network.SyncBackInventory;
import com.beansgalaxy.backpacks.client.network.SyncBackSlot;
import com.beansgalaxy.backpacks.client.network.SyncViewersPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ReceiveAtClient {
      public static void SyncBackInventory(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
            String stacks = buf.readUtf();
            SyncBackInventory.receiveAtClient(stacks);
      }

      public static void SyncBackSlot(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf byteBuf, PacketSender packetSender) {
            UUID id = byteBuf.readUUID();
            ItemStack stack = byteBuf.readItem();
            SyncBackSlot.receiveAtClient(id, stack);
      }

      public static void SyncViewers(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf byteBuf, PacketSender packetSender) {
            int id = byteBuf.readInt();
            byte viewers = byteBuf.readByte();
            SyncViewersPacket.receiveAtClient(id, viewers);
      }
}
