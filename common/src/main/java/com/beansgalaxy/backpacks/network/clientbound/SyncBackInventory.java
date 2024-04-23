package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SyncBackInventory implements Packet2C {
      final String stacks;

      public SyncBackInventory(BackpackInventory backpackInventory) {
            CompoundTag compound = new CompoundTag();
            backpackInventory.writeNbt(compound);
            this.stacks = compound.getAsString();
      }

      public SyncBackInventory(FriendlyByteBuf buf) {
            stacks = buf.readUtf();
      }

      public static void send(ServerPlayer owner) {
            BackData backData = BackData.get(owner);
            ItemStack backStack = backData.getStack();
            if (backStack.getItem() instanceof EnderBackpack backpack) {
                  if (!backStack.isEmpty())
                        SendEnderData.send(owner, backpack.getOrCreateUUID(owner.getUUID(), backStack));
                  return;
            }

            Services.NETWORK.send(Network2C.SYNC_BACK_INV_2C, new SyncBackInventory(backData.backpackInventory), owner);
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.SYNC_BACK_INV_2C.debugMsgEncode();
            buf.writeUtf(stacks);
      }

      @Override
      public void handle() {
            Network2C.SYNC_BACK_INV_2C.debugMsgDecode();
            CommonAtClient.syncBackInventory(stacks);
      }

}
