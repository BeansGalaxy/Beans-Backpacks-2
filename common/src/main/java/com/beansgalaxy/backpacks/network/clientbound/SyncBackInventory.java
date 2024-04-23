package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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
