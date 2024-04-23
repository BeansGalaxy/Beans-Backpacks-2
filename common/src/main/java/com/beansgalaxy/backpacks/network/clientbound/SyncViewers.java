package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;

public class SyncViewers implements Packet2C {
      int entityId;
      byte viewers;

      public SyncViewers(int entityId, byte viewers) {
            this.entityId = entityId;
            this.viewers = viewers;
      }

      public SyncViewers(FriendlyByteBuf byteBuf) {
            this(byteBuf.readInt(), byteBuf.readByte());
      }

      @Override
      public void encode(FriendlyByteBuf byteBuf) {
            Network2C.SYNC_VIEWERS_2C.debugMsgEncode();
            byteBuf.writeInt(entityId);
            byteBuf.writeByte(viewers);
      }

      @Override
      public void handle() {
            Network2C.SYNC_VIEWERS_2C.debugMsgDecode();
            CommonAtClient.syncViewersPacket(entityId, viewers);
      }
}
