package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SyncBackSlot implements Packet2C {
      final UUID uuid;
      final ItemStack stack;

      public SyncBackSlot(UUID uuid, ItemStack stack) {
            this.uuid = uuid;
            this.stack = stack;
      }

      public SyncBackSlot(FriendlyByteBuf buf) {
            this(buf.readUUID(), buf.readItem());
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.SYNC_BACK_SLOT_2C.debugMsgEncode();
            buf.writeUUID(uuid);
            buf.writeItem(stack);
      }

      @Override
      public void handle() {
            Network2C.SYNC_BACK_SLOT_2C.debugMsgDecode();
            CommonAtClient.syncBackSlot(uuid, stack);
      }
}
