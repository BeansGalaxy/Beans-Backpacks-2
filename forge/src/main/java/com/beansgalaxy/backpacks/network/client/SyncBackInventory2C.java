package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.client.network.SyncBackInventory;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class SyncBackInventory2C {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(SyncBackInventory2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncBackInventory2C::encode).decoder(SyncBackInventory2C::new).consumerMainThread(SyncBackInventory2C::handle).add();
      }

      final String stacks;

      public SyncBackInventory2C(String stacks) {
            this.stacks = stacks;
      }

      public SyncBackInventory2C(FriendlyByteBuf buf) {
            this(buf.readUtf());
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(stacks);
      }

      public void handle(CustomPayloadEvent.Context context) {
            SyncBackInventory.receiveAtClient(stacks);
      }

}
