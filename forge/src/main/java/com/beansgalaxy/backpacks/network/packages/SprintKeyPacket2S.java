package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class SprintKeyPacket2S {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(SprintKeyPacket2S.class, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(SprintKeyPacket2S::encode).decoder(SprintKeyPacket2S::new).consumerMainThread(SprintKeyPacket2S::handle).add();
      }

      boolean sprintKeyPressed;

      public SprintKeyPacket2S(boolean sprintKeyPressed) {
            this.sprintKeyPressed = sprintKeyPressed;
      }

      public SprintKeyPacket2S(FriendlyByteBuf byteBuf) {
            this(byteBuf.readBoolean());
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(this.sprintKeyPressed);
      }

      public void handle(CustomPayloadEvent.Context context) {
            ServerPlayer player = context.getSender();
            BackData.get(player).actionKeyPressed = sprintKeyPressed;
      }
}
