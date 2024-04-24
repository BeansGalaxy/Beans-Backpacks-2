package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class CallBackInventory implements Packet2S {
      final UUID uuid;

      public CallBackInventory(FriendlyByteBuf buf) {
            this(buf.readUUID());
      }

      public CallBackInventory(UUID uuid) {
            this.uuid = uuid;
      }

      public static void send(UUID uuid) {
            new CallBackInventory(uuid).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.CALL_BACK_INV_2S;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            SyncBackInventory.send(sender);
      }
}
