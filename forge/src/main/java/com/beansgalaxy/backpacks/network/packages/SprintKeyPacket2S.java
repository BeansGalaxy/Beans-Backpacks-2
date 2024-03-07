package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SprintKeyPacket2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(SprintKeyPacket2S.class, i, NetworkDirection.PLAY_TO_SERVER)
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

      public void handle(Supplier<NetworkEvent.Context> context) {
            ServerPlayer player = context.get().getSender();
            BackData.get(player).actionKeyPressed = sprintKeyPressed;
      }
}
