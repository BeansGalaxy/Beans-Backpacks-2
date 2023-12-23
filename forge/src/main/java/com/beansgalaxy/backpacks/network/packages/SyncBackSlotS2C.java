package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.client.network.SyncBackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

public class SyncBackSlotS2C {
      final UUID uuid;
      final ItemStack stack;

      public SyncBackSlotS2C(UUID uuid, ItemStack stack) {
            this.uuid = uuid;
            this.stack = stack;
      }

      public SyncBackSlotS2C(FriendlyByteBuf buf) {
            this(buf.readUUID(), buf.readItem());
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeItem(stack);
      }

      public void handle(CustomPayloadEvent.Context context) {
            SyncBackSlot.receiveAtClient(uuid, stack);
      }
}
