package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.items.BackpackItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.BlockHitResult;

public class InstantPlace2S {
      public static void receiveAtServer(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler,
                                         FriendlyByteBuf buf, PacketSender responseSender) {
            int entityId = buf.readInt();
            if (entityId == -1)
            {
                  BlockHitResult blockHitResult = buf.readBlockHitResult();
                  BackpackItem.hotkeyOnBlock(serverPlayer, blockHitResult.getDirection(), blockHitResult.getBlockPos());
            }
            else if (serverPlayer.level().getEntity(entityId) instanceof BackpackEntity backpack)
            {
                  backpack.interact(serverPlayer);
            }
      }
}
