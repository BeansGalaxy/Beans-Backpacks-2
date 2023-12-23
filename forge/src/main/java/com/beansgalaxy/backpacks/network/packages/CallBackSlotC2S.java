package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

public class CallBackSlotC2S {
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
