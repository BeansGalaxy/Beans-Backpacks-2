package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SyncBackSlot implements Packet2C {
      private static final HashMap<Integer, HeldSlot> HELD_BACK_SLOT = new HashMap<>();
      public int failedSyncAttempts = 0;
      public final int entity;
      public final ItemStack stack;

      public SyncBackSlot(int entity, ItemStack stack) {
            this.entity = entity;
            this.stack = stack;
      }

      public SyncBackSlot(FriendlyByteBuf buf) {
            this(buf.readInt(), buf.readItem());
      }


      public static void send(Player owner, ServerPlayer sender) {
            ItemStack stack = BackData.get(owner).getStack();
            new SyncBackSlot(owner.getId(), stack).send2C(sender);
      }

      public static void send(ServerPlayer owner) {
            ItemStack stack = BackData.get(owner).getStack();
            new SyncBackSlot(owner.getId(), stack).send2A(owner.level().getServer());
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.SYNC_BACK_SLOT_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(entity);
            buf.writeItem(stack);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            if (!CommonAtClient.syncBackSlot(entity, stack))
                  HELD_BACK_SLOT.put(entity, new HeldSlot(stack));
      }

      private static class HeldSlot {
            final ItemStack stack;
            int attempts = 0;

            HeldSlot(ItemStack stack) {
                  this.stack = stack;
            }
      }

      public static void indexHeldSlots() {
            Iterator<Integer> iterator = HELD_BACK_SLOT.keySet().iterator();
            for (int i = 0; iterator.hasNext() && i < 32; i++) {
                  Integer entity = iterator.next();
                  HeldSlot ctx = HELD_BACK_SLOT.get(entity);
                  Constants.LOG.info("Indexing Held BackSlot...  E:" + entity + "  A:" + ctx.attempts);
                  if (ctx.attempts > 32 || CommonAtClient.syncBackSlot(entity, ctx.stack)) {
                        iterator.remove();
                  }
                  else ctx.attempts++;
            }
      }
}
