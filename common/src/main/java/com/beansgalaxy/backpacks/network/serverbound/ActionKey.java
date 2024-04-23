package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ActionKey implements Packet2S {

      private final boolean actionKeyPressed;

      public ActionKey(boolean actionKeyPressed) {
            this.actionKeyPressed = actionKeyPressed;
      }

      public ActionKey(FriendlyByteBuf buf) {
            this(buf.readBoolean());
      }

      public static void send(boolean actionKeyPressed) {
            Services.NETWORK.send(Network2S.ACTION_KEY_2S, new ActionKey(actionKeyPressed));
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            Network2S.ACTION_KEY_2S.debugMsgEncode();
            buf.writeBoolean(actionKeyPressed);
      }

      @Override
      public void handle(ServerPlayer sender) {
            Network2S.ACTION_KEY_2S.debugMsgDecode();
            BackData.get(sender).actionKeyPressed = actionKeyPressed;
      }
}
