package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class UseCauldron implements Packet2S{
      final BlockPos blockPos;
      final UseKeyEvent.Type type;

      public UseCauldron(BlockPos pos, UseKeyEvent.Type type) {
            blockPos = pos;
            this.type = type;
      }

      public UseCauldron(FriendlyByteBuf byteBuf) {
            this(byteBuf.readBlockPos(), UseKeyEvent.Type.byID(byteBuf.readByte()));
      }

      public static void send(BlockPos blockPos, UseKeyEvent.Type type) {
            new UseCauldron(blockPos, type).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.USE_CAULDRON_2S;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(blockPos);
            buf.writeByte(type.id);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            Level level = sender.level();
            BackData backData = BackData.get(sender);

            switch (type) {
                  case PICKUP -> UseKeyEvent.cauldronPickup(sender, blockPos, level, backData);
                  case PLACE -> UseKeyEvent.cauldronPlace(level, blockPos, level.getBlockState(blockPos), backData);
                  case EQUIP -> UseKeyEvent.potCauldronEquip(blockPos, level, backData);
            }
      }
}
