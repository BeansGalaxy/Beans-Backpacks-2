package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SendEnderData implements Packet2C {
      final UUID uuid;
      final EnderStorage.Data enderData;

      public SendEnderData(FriendlyByteBuf buf) {
            this.uuid = buf.readUUID();
            NonNullList<ItemStack> stacks = NonNullList.create();
            BackpackInventory.readStackNbt(buf.readNbt(), stacks);
            CompoundTag trim = buf.readNbt();
            MutableComponent playerName = Component.Serializer.fromJson(buf.readUtf());
            this.enderData = new EnderStorage.Data(stacks, trim, playerName);
      }

      public SendEnderData(UUID uuid, EnderStorage.Data enderData) {
            this.uuid = uuid;
            this.enderData = enderData;
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.SEND_ENDER_DATA_2C.debugMsgEncode();
            buf.writeUUID(uuid);

            CompoundTag tag = new CompoundTag();
            BackpackInventory.writeNbt(tag, enderData.getItemStacks());
            buf.writeNbt(tag);

            buf.writeNbt(enderData.getTrim());
            Component playerName = enderData.getPlayerName();
            buf.writeUtf(Component.Serializer.toJson(playerName));
      }

      @Override
      public void handle() {
            Network2C.SEND_ENDER_DATA_2C.debugMsgDecode();
            EnderStorage.Data computed = EnderStorage.get().MAPPED_DATA.computeIfAbsent(uuid, in -> new EnderStorage.Data());
            computed.setPlayerName(enderData.getPlayerName()).setTrim(enderData.getTrim()).setItemStacks(enderData.getItemStacks());
      }
}
