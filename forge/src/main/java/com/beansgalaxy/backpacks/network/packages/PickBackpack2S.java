package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.events.PickBlockEvent;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PickBackpack2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(PickBackpack2S.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(PickBackpack2S::encode).decoder(PickBackpack2S::new).consumerMainThread(PickBackpack2S::handle).add();
      }

      int slot;

      public PickBackpack2S(int slot) {
            this.slot = slot;
      }

      public PickBackpack2S(FriendlyByteBuf byteBuf) {
            this.slot = byteBuf.readInt();

      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(slot);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            PickBlockEvent.pickBackpack(slot, context.get().getSender());
      }
}
