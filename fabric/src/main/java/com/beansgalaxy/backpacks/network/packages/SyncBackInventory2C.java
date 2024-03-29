package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SyncBackInventory2C {
      public static void S2C(ServerPlayer owner) {
            BackpackInventory backpackInventory = BackData.get(owner).backpackInventory;
            FriendlyByteBuf buf = PacketByteBufs.create();
            CompoundTag compound = new CompoundTag();
            backpackInventory.writeNbt(compound);
            String stacks = compound.getAsString();
            buf.writeUtf(stacks);
            ServerPlayNetworking.send(owner, NetworkPackages.SYNC_BACK_INV_2C, buf);
      }

      public static void callSyncBackInventory(MinecraftServer server, ServerPlayer thisPlayer, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            S2C(thisPlayer);
      }
}
