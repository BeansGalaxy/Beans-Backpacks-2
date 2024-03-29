package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;

public class UseCauldron2S {
      public static void receiveAtServer(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler,
                                         FriendlyByteBuf buf, PacketSender responseSender) {
            BlockPos blockPos = buf.readBlockPos();
            UseKeyEvent.Type type = UseKeyEvent.Type.byID(buf.readByte());
            Level level = sender.level();
            BackData backData = BackData.get(sender);

           switch (type) {
                 case PICKUP -> UseKeyEvent.cauldronPickup(sender, blockPos, level, backData);
                 case PLACE -> UseKeyEvent.cauldronPlace(level, blockPos, level.getBlockState(blockPos), backData);
                 case EQUIP -> UseKeyEvent.potCauldronEquip(blockPos, level, backData);
           }
      }
}
