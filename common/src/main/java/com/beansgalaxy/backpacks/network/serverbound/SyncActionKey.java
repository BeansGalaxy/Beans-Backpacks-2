package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2S;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SyncActionKey implements Packet2S {

      private final boolean actionKeyPressed;
      private final boolean menuKeyPressed;

      public SyncActionKey(boolean actionPressed, boolean menuPressed) {
            actionKeyPressed = actionPressed;
            menuKeyPressed = menuPressed;
      }

      public SyncActionKey(FriendlyByteBuf buf) {
            this(buf.readBoolean(), buf.readBoolean());
      }

      public static void send(boolean actionPressed, boolean menuPressed) {
            new SyncActionKey(actionPressed, menuPressed).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.ACTION_KEY_2S;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(actionKeyPressed);
            buf.writeBoolean(menuKeyPressed);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            BackData backData = BackData.get(sender);
            backData.actionKeyDown = actionKeyPressed;
            backData.menusKeyDown = menuKeyPressed;
      }
}
