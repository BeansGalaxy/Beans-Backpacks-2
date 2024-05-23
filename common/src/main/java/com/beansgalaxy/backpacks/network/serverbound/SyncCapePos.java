package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.config.BackpackCapePos;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.SendCapePos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SyncCapePos implements Packet2S {
      private final BackpackCapePos capePos;

      private SyncCapePos(BackpackCapePos pos) {
            this.capePos = pos;
      }

      public SyncCapePos(FriendlyByteBuf buf) {
            this(BackpackCapePos.fromIndex(buf.readByte()));
      }

      public static void send(BackpackCapePos pos) {
            new SyncCapePos(pos).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.SYNC_CAPE_POS;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeByte(capePos.index);
      }

      @Override
      public void handle(ServerPlayer sender) {
            BackData.get(sender).capePos = capePos;
            SendCapePos.send(capePos, sender);
      }
}
