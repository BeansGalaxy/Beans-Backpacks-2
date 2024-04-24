package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SyncBackSlot implements Packet2C {
      final UUID uuid;
      final ItemStack stack;

      public SyncBackSlot(UUID uuid, ItemStack stack) {
            this.uuid = uuid;
            this.stack = stack;
      }

      public SyncBackSlot(FriendlyByteBuf buf) {
            this(buf.readUUID(), buf.readItem());
      }


      public static void send(Player owner, ServerPlayer sender) {
            new SyncBackSlot(owner.getUUID(), BackData.get(owner).getStack()).send2C(sender);
      }

      public static void send(Player owner) {
            new SyncBackSlot(owner.getUUID(), BackData.get(owner).getStack()).send2A(owner.level().getServer());
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.SYNC_BACK_SLOT_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeItem(stack);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.syncBackSlot(uuid, stack);
      }
}
