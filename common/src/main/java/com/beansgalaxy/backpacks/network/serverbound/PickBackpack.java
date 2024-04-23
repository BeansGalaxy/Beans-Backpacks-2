package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.events.PickBlockEvent;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PickBackpack implements Packet2S {

      int slot;

      public PickBackpack(int slot) {
            this.slot = slot;
      }

      public PickBackpack(FriendlyByteBuf byteBuf) {
            this.slot = byteBuf.readInt();

      }

      public static void send(int slot) {
            Services.NETWORK.send(Network2S.PICK_BACKPACK_2S, new PickBackpack(slot));
      }

      public void encode(FriendlyByteBuf buf) {
            Network2S.PICK_BACKPACK_2S.debugMsgEncode();
            buf.writeInt(slot);
      }

      @Override
      public void handle(ServerPlayer sender) {
            Network2S.PICK_BACKPACK_2S.debugMsgDecode();
            PickBlockEvent.pickBackpack(slot, sender);
      }
}
