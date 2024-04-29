package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SendEnderViewing implements Packet2C {
      public final UUID uuid;
      private final byte viewers;

      public SendEnderViewing(FriendlyByteBuf buf) {
            this.uuid = buf.readUUID();
            this.viewers = buf.readByte();
      }

      public SendEnderViewing(UUID uuid, Viewable viewable) {
            this.uuid = uuid;
            this.viewers = viewable.getViewers();
      }

      public static void send(ServerPlayer player, UUID uuid) {
            EnderInventory enderData = EnderStorage.getEnderData(uuid, player.level());
            new SendEnderViewing(uuid, enderData.getViewable()).send2C(player);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.ENDER_VIEWING_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeByte(viewers);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.sendEnderData(uuid, in -> {
                  in.getViewable().setViewers(this.viewers);
            });
      }
}
