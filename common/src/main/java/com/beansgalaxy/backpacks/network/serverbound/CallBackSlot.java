package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CallBackSlot implements Packet2S {
      final UUID uuid;

      public CallBackSlot(FriendlyByteBuf buf) {
            this(buf.readUUID());
      }

      public CallBackSlot(UUID uuid) {
            this.uuid = uuid;
      }

      public static void send(UUID uuid) {
            Services.NETWORK.send(Network2S.CALL_BACK_SLOT_2S, new CallBackSlot(uuid));
      }

      public void encode(FriendlyByteBuf buf) {
            Network2S.CALL_BACK_SLOT_2S.debugMsgEncode();
            buf.writeUUID(uuid);
      }

      @Override
      public void handle(ServerPlayer sender) {
            Network2S.CALL_BACK_SLOT_2S.debugMsgDecode();
            Player owner = sender.level().getPlayerByUUID(uuid);
            SyncBackSlot.send(owner, sender);
      }
}
