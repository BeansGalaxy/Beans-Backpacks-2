package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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

      public static void send(Entity owner, byte viewers) {
            int id = owner.getId();
            MinecraftServer server = owner.level().getServer();
            Services.NETWORK.send(Network2C.SYNC_VIEWERS_2C, new SyncViewers(id, viewers), server);
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
