package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2S;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearBackSlot implements Packet2S{

      public ClearBackSlot(BackData backData) {
            backData.getBackpackInventory().clearContent();
            backData.set(ItemStack.EMPTY);
      }

      public ClearBackSlot(FriendlyByteBuf byteBuf) {
            byteBuf.readBoolean();
      }

      public static void send(BackData backData) {
            new ClearBackSlot(backData).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.CLEAR_BACK_SLOT_2S;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(true);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            if (sender != null) {
                  BackData backData = BackData.get(sender);
                  backData.getBackpackInventory().clearContent();
                  backData.set(ItemStack.EMPTY);
            }
      }
}
