package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SendEnderData2C {

      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(SendEnderData2C.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SendEnderData2C::encode).decoder(SendEnderData2C::new).consumerMainThread(SendEnderData2C::handle).add();
      }

      final UUID uuid;
      final EnderStorage.Data enderData;

      public SendEnderData2C(FriendlyByteBuf buf) {
            this.uuid = buf.readUUID();
            NonNullList<ItemStack> stacks = NonNullList.create();
            BackpackInventory.readStackNbt(buf.readNbt(), stacks);
            CompoundTag trim = buf.readNbt();
            MutableComponent playerName = Component.Serializer.fromJson(buf.readUtf());
            this.enderData = new EnderStorage.Data(stacks, trim, playerName);
      }

      public SendEnderData2C(UUID uuid, EnderStorage.Data enderData) {
            this.uuid = uuid;
            this.enderData = enderData;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);

            CompoundTag tag = new CompoundTag();
            BackpackInventory.writeNbt(tag, enderData.getItemStacks());
            buf.writeNbt(tag);

            buf.writeNbt(enderData.getTrim());
            MutableComponent playerName = enderData.getPlayerName();
            buf.writeUtf(Component.Serializer.toJson(playerName));
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            EnderStorage.Data computed = ServerSave.MAPPED_ENDER_DATA.computeIfAbsent(uuid, in -> new EnderStorage.Data());
            computed.setPlayerName(enderData.getPlayerName()).setTrim(enderData.getTrim()).setItemStacks(enderData.getItemStacks());
      }
}
