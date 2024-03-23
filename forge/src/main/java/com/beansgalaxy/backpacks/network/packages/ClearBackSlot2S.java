package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearBackSlot2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(ClearBackSlot2S.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(ClearBackSlot2S::encode).decoder(ClearBackSlot2S::new).consumerMainThread(ClearBackSlot2S::handle).add();
      }

      public ClearBackSlot2S() {
      }

      public ClearBackSlot2S(FriendlyByteBuf byteBuf) {
            byteBuf.readBoolean();
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(true);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            ServerPlayer sender = context.get().getSender();
            if (sender != null)
                  BackData.get(sender).set(ItemStack.EMPTY);
      }
}
