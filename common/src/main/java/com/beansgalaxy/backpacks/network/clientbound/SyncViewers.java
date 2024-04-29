package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

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

      public static void send(BackpackInventory backpackInventory) {
            int id = backpackInventory.getOwner().getId();
            MinecraftServer server = backpackInventory.level().getServer();
            if (backpackInventory instanceof EnderInventory ender)
                  new SendEnderViewing(ender.getPlacedBy(), ender.getViewable()).send2A(server);
            else
                  new SyncViewers(id, backpackInventory.getViewable().getViewers()).send2A(server);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.SYNC_VIEWERS_2C;
      }

      @Override
      public void encode(FriendlyByteBuf byteBuf) {
            byteBuf.writeInt(entityId);
            byteBuf.writeByte(viewers);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.syncViewersPacket(entityId, viewers);
      }
}
