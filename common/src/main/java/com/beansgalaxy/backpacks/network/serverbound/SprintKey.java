package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2S;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SprintKey implements Packet2S {

      private final boolean actionKeyPressed;

      public SprintKey(boolean actionKeyPressed) {
            this.actionKeyPressed = actionKeyPressed;
      }

      public SprintKey(FriendlyByteBuf buf) {
            this(buf.readBoolean());
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            Network2S.SPRINT_KEY_2S.debugMsgEncode();
            buf.writeBoolean(actionKeyPressed);
      }

      @Override
      public void handle(ServerPlayer sender) {
            Network2S.SPRINT_KEY_2S.debugMsgDecode();
            BackData.get(sender).actionKeyPressed = actionKeyPressed;
      }
}
