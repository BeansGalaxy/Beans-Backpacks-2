package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.client.SyncBackSlotS2C;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.UUID;

public class CallBackSlotC2S {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(CallBackSlotC2S.class, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(CallBackSlotC2S::encode).decoder(CallBackSlotC2S::new).consumerMainThread(CallBackSlotC2S::handle).add();
      }

      final UUID uuid;

      public CallBackSlotC2S(FriendlyByteBuf buf) {
            this(buf.readUUID());
      }

      public CallBackSlotC2S(UUID uuid) {
            this.uuid = uuid;
      }

      public static void call(Player player) {
            UUID uuid = player.getUUID();
            NetworkPackages.C2S(new CallBackSlotC2S(uuid));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
      }

      public void handle(CustomPayloadEvent.Context context) {
            Player otherPlayer = context.getSender().level().getPlayerByUUID(uuid);
            ItemStack stack = BackSlot.get(otherPlayer).getItem();
            NetworkPackages.S2C(new SyncBackSlotS2C(uuid, stack), context.getSender());
      }
}
