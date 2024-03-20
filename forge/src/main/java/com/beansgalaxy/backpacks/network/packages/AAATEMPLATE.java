package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AAATEMPLATE {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(AAATEMPLATE.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(AAATEMPLATE::encode).decoder(AAATEMPLATE::new).consumerMainThread(AAATEMPLATE::handle).add();
      }

      public AAATEMPLATE() {
      }

      public AAATEMPLATE(FriendlyByteBuf byteBuf) {
      }

      public void encode(FriendlyByteBuf buf) {
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
      }
}
