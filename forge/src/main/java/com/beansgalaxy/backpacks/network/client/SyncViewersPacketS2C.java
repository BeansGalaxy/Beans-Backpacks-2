package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.SyncViewersPacket;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class SyncViewersPacketS2C {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(SyncViewersPacketS2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncViewersPacketS2C::encode).decoder(SyncViewersPacketS2C::new).consumerMainThread(SyncViewersPacketS2C::handle).add();
      }

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
