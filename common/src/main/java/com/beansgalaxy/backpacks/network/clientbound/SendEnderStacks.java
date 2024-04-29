package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class SendEnderStacks implements Packet2C {
      public final UUID uuid;
      public final NonNullList<ItemStack> itemStacks;

      public SendEnderStacks(FriendlyByteBuf buf) {
            this.uuid = buf.readUUID();
            this.itemStacks = NonNullList.create();
            BackpackInventory.readStackNbt(buf.readNbt(), itemStacks);
      }

      public SendEnderStacks(UUID uuid, CompoundTag trim, NonNullList<ItemStack> itemStacks) {
            this.uuid = uuid;
            this.itemStacks = itemStacks;
      }

      public static void send(ServerPlayer player, UUID uuid) {
            EnderInventory enderData = EnderStorage.getEnderData(uuid, player.level());
            new SendEnderStacks(uuid, enderData.getTrim(), enderData.getItemStacks()).send2C(player);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.ENDER_INV_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);

            CompoundTag tag = new CompoundTag();
            BackpackInventory.writeNbt(tag, itemStacks);
            buf.writeNbt(tag);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.sendEnderData(uuid, in -> in.setItemStacks(itemStacks));
      }
}
