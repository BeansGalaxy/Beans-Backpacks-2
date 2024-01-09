package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.client.SyncBackSlot2C;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CallBackSlot2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(CallBackSlot2S.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(CallBackSlot2S::encode).decoder(CallBackSlot2S::new).consumerMainThread(CallBackSlot2S::handle).add();
      }

      final UUID uuid;

      public CallBackSlot2S(FriendlyByteBuf buf) {
            this(buf.readUUID());
      }

      public CallBackSlot2S(UUID uuid) {
            this.uuid = uuid;
      }

      public static void call(Player player) {
            UUID uuid = player.getUUID();
            NetworkPackages.C2S(new CallBackSlot2S(uuid));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            Player otherPlayer = context.get().getSender().level().getPlayerByUUID(uuid);
            ItemStack stack = BackSlot.get(otherPlayer).getItem();
            NetworkPackages.S2C(new SyncBackSlot2C(uuid, stack), context.get().getSender());
      }
}
