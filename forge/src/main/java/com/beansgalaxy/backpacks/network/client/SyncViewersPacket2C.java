package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.SyncViewersPacket;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncViewersPacket2C {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(SyncViewersPacket2C.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncViewersPacket2C::encode).decoder(SyncViewersPacket2C::new).consumerMainThread(SyncViewersPacket2C::handle).add();
      }

      int entityId;
      byte viewers;

      public SyncViewersPacket2C(int entityId, byte viewers) {
            this.entityId = entityId;
            this.viewers = viewers;
      }

      public SyncViewersPacket2C(FriendlyByteBuf byteBuf) {
            this(byteBuf.readInt(), byteBuf.readByte());
      }

      public void encode(FriendlyByteBuf byteBuf) {
            byteBuf.writeInt(entityId);
            byteBuf.writeByte(viewers);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            SyncViewersPacket.receiveAtClient(entityId, viewers);
      }
}
