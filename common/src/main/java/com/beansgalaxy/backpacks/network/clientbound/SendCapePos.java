package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.config.BackpackCapePos;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SendCapePos implements Packet2C {
      private final int player;
      private final byte capePos;

      private SendCapePos(int id, byte index) {
            player = id;
            capePos = index;
      }

      public SendCapePos(FriendlyByteBuf buf) {
            this(buf.readInt(), buf.readByte());
      }

      public static void send(BackpackCapePos capePos, ServerPlayer owner) {
            new SendCapePos(owner.getId(), capePos.index).send2A(owner.getServer());
      }

      public static void send(BackpackCapePos capePos, ServerPlayer owner, ServerPlayer listener) {
            new SendCapePos(owner.getId(), capePos.index).send2C(listener);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.SEND_CAPE_POS;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(player);
            buf.writeByte(capePos);
      }

      @Override
      public void handle() {
            CommonAtClient.receiveCapePos(player, capePos);
      }
}
