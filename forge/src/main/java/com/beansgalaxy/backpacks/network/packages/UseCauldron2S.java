package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseCauldron2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(UseCauldron2S.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(UseCauldron2S::encode).decoder(UseCauldron2S::new).consumerMainThread(UseCauldron2S::handle).add();
      }

      final BlockPos blockPos;
      final UseKeyEvent.Type type;

      public UseCauldron2S(BlockPos pos, UseKeyEvent.Type type) {
            blockPos = pos;
            this.type = type;
      }

      public UseCauldron2S(FriendlyByteBuf byteBuf) {
            this(byteBuf.readBlockPos(), UseKeyEvent.Type.byID(byteBuf.readByte()));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(blockPos);
            buf.writeByte(type.id);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            ServerPlayer sender = context.get().getSender();
            Level level = sender.level();
            BackData backData = BackData.get(sender);

            switch (type) {
                  case PICKUP -> UseKeyEvent.cauldronPickup(sender, blockPos, level, backData);
                  case PLACE -> UseKeyEvent.cauldronPlace(level, blockPos, level.getBlockState(blockPos), backData);
                  case EQUIP -> UseKeyEvent.potCauldronEquip(blockPos, level, backData);
            }
      }
}
