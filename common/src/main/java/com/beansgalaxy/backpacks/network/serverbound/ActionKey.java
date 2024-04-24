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
            new ActionKey(actionKeyPressed).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.ACTION_KEY_2S;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(actionKeyPressed);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            BackData.get(sender).actionKeyPressed = actionKeyPressed;
      }
}
