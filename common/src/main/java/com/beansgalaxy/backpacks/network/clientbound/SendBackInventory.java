package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SendBackInventory implements Packet2C {
      final CompoundTag stacks;
      final int container;

      public SendBackInventory(BackpackInventory backpackInventory, int container) {
            CompoundTag compound = new CompoundTag();
            backpackInventory.writeNbt(compound);
            stacks = compound;
            this.container = container;
      }

      public SendBackInventory(FriendlyByteBuf buf) {
            container = buf.readInt();
            stacks = buf.readNbt();
      }

      public static void send(ServerPlayer owner) {
            BackData backData = BackData.get(owner);
            ItemStack backStack = backData.getStack();
            if (backStack.getItem() instanceof EnderBackpack backpack) {
                  if (!backStack.isEmpty())
                        SendEnderStacks.send(owner, backpack.getOrCreateUUID(owner, backStack));
                  return;
            }

            new SendBackInventory(backData.getBackpackInventory(), -1).send2C(owner);
      }

      public static void send(BackpackMenu menu) {
            BackpackInventory backpackInventory = menu.backpackInventory;
            SendBackInventory msg = new SendBackInventory(backpackInventory, menu.containerId);
            for (ServerPlayer player : backpackInventory.getPlayersViewing())
                  msg.send2C(player);
      }


      @Override
      public Network2C getNetwork() {
            return Network2C.SYNC_BACK_INV_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(container);
            buf.writeNbt(stacks);
      }


      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            if (!CommonAtClient.syncBackInventory(stacks, container))
                  heldInv = stacks;
      }

      private static CompoundTag heldInv = null;
      public static void indexInventories() {
            if (heldInv != null && CommonAtClient.syncBackInventory(heldInv, -1))
                  heldInv = null;
      }
}
