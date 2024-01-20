package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncBackSlot2C {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(SyncBackSlot2C.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncBackSlot2C::encode).decoder(SyncBackSlot2C::new).consumerMainThread(SyncBackSlot2C::handle).add();
      }

      final UUID uuid;
      final ItemStack stack;

      public SyncBackSlot2C(UUID uuid, ItemStack stack) {
            this.uuid = uuid;
            this.stack = stack;
      }

      public SyncBackSlot2C(FriendlyByteBuf buf) {
            this(buf.readUUID(), buf.readItem());
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeItem(stack);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            CommonAtClient.syncBackSlot(uuid, stack);
      }
}
