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
            new PickBackpack(slot).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.PICK_BACKPACK_2S;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(slot);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            PickBlockEvent.pickBackpack(slot, sender);
      }
}
