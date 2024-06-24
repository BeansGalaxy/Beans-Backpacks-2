package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EquipLockedMsg implements Packet2C {
      private final Component viewer;
      private EquipLockedMsg(Component viewer) {
            this.viewer = viewer;
      }

      public EquipLockedMsg(FriendlyByteBuf buf) {
            this.viewer = buf.readComponent();
      }

      public static void send(Player viewer, ServerPlayer owner) {
            Component displayName = viewer.getDisplayName();
            new EquipLockedMsg(displayName).send2C(owner);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.EQUIP_LOCKED_MSG;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeComponent(viewer);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.receiveEquipLockMsg(viewer);
      }
}
