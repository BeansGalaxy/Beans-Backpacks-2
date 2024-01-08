package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class SyncBackSlot2All {
      public static void S2All(ServerPlayer owner, ItemStack stack) {
            Level world = owner.level();
            List<ServerPlayer> playerList = world.getServer().getPlayerList().getPlayers();
            for (ServerPlayer serverPlayer : playerList)
                  if (serverPlayer.level().dimension() == world.dimension()) {
                        FriendlyByteBuf buf = PacketByteBufs.create();
                        buf.writeUUID(owner.getUUID());
                        buf.writeItem(stack);

                        ServerPlayNetworking.send(serverPlayer, NetworkPackages.SYNC_BACK_SLOT_2C, buf);
                  }
      }

      public static void callSyncBackSlot(MinecraftServer server, ServerPlayer thisPlayer, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            UUID uuid = buf.readUUID();
            Player otherPlayer = thisPlayer.level().getPlayerByUUID(uuid);
            ItemStack backStack = BackSlot.get(otherPlayer).getItem();

            FriendlyByteBuf bufSlot = PacketByteBufs.create();
            bufSlot.writeUUID(uuid);
            bufSlot.writeItem(backStack);

            ServerPlayNetworking.send(thisPlayer, NetworkPackages.SYNC_BACK_SLOT_2C, bufSlot);
      }
}
