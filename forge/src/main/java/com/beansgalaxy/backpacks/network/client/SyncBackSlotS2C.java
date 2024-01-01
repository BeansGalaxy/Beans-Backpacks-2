package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.SyncBackSlot;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.UUID;

public class SyncBackSlotS2C {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(SyncBackSlotS2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncBackSlotS2C::encode).decoder(SyncBackSlotS2C::new).consumerMainThread(SyncBackSlotS2C::handle).add();
      }

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
