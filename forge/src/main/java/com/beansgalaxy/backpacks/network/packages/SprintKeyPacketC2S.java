package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SprintKeyPacketC2S {
      boolean sprintKeyPressed;

      public SprintKeyPacketC2S(boolean sprintKeyPressed) {
            this.sprintKeyPressed = sprintKeyPressed;
      }

      public SprintKeyPacketC2S(FriendlyByteBuf byteBuf) {
            this(byteBuf.readBoolean());
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(this.sprintKeyPressed);
      }

      public void handle(CustomPayloadEvent.Context context) {
            ServerPlayer player = context.getSender();
            BackSlot.get(player).sprintKeyIsPressed = sprintKeyPressed;
      }
}
