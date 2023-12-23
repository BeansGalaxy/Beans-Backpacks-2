package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.client.network.SyncViewersPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncViewersPacketS2C {
      int entityId;
      byte viewers;

      public SyncViewersPacketS2C(int entityId, byte viewers) {
            this.entityId = entityId;
            this.viewers = viewers;
      }

      public SyncViewersPacketS2C(FriendlyByteBuf byteBuf) {
            this(byteBuf.readInt(), byteBuf.readByte());
      }

      public void encode(FriendlyByteBuf byteBuf) {
            byteBuf.writeInt(entityId);
            byteBuf.writeByte(viewers);
      }

      public void handle(CustomPayloadEvent.Context context) {
            SyncViewersPacket.receiveAtClient(entityId, viewers);
      }
}
