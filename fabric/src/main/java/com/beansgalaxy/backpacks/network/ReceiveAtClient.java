package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.data.EnderStorage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ReceiveAtClient {
      public static void SyncBackInventory(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
            String stacks = buf.readUtf();
            CommonAtClient.syncBackInventory(stacks);
      }

      public static void syncBackSlot(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf byteBuf, PacketSender packetSender) {
            UUID id = byteBuf.readUUID();
            ItemStack stack = byteBuf.readItem();
            CommonAtClient.syncBackSlot(id, stack);
      }

      public static void SyncViewers(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf byteBuf, PacketSender packetSender) {
            int id = byteBuf.readInt();
            byte viewers = byteBuf.readByte();
            CommonAtClient.syncViewersPacket(id, viewers);
      }

      public static void ConfigBackpackData(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
            Map<String, CompoundTag> map = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readNbt);
            for (String key: map.keySet())
            {
                  CompoundTag tag = map.get(key);
                  Traits traits = new Traits(tag);

                  Traits.register(key, traits);
            }
      }

      public static void recieveEnderPos(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
            LocalPlayer player = minecraft.player;
            if (player == null)
                  return;

            HashSet<EnderStorage.Location> newLocations = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--)
                  newLocations.add(new EnderStorage.Location(buf));

            BackData backData = BackData.get(player);
            backData.setEnderLocations(newLocations);

      }
}
