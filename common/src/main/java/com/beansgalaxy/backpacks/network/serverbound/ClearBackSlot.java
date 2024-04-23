package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2S;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearBackSlot implements Packet2S{

      public ClearBackSlot(BackData backData) {
            backData.backpackInventory.clearContent();
            backData.set(ItemStack.EMPTY);
      }

      public ClearBackSlot(FriendlyByteBuf byteBuf) {
            byteBuf.readBoolean();
      }

      public void encode(FriendlyByteBuf buf) {
            Network2S.CLEAR_BACK_SLOT_2S.debugMsgEncode();
            buf.writeBoolean(true);
      }

      @Override
      public void handle(ServerPlayer sender) {
            Network2S.CLEAR_BACK_SLOT_2S.debugMsgDecode();
            if (sender != null) {
                  BackData backData = BackData.get(sender);
                  backData.backpackInventory.clearContent();
                  backData.set(ItemStack.EMPTY);
            }
      }
}
