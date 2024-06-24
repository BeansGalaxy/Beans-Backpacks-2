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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SendEnderDisplay implements Packet2C {
      public final UUID uuid;
      public final Component playerName;
      public final CompoundTag trim;
      private final boolean locked;

      public SendEnderDisplay(FriendlyByteBuf buf) {
            this.uuid = buf.readUUID();
            this.trim = buf.readNbt();
            this.playerName = buf.readComponent();
            this.locked = buf.readBoolean();
      }

      public SendEnderDisplay(UUID uuid, CompoundTag trim, Component playerName, boolean locked) {
            this.uuid = uuid;
            this.trim = trim;
            this.playerName = playerName;
            this.locked = locked;
      }

      public static void send(ServerPlayer sender, UUID owner) {
            EnderInventory enderData = EnderStorage.getEnderData(owner, sender.level());
            new SendEnderDisplay(owner, enderData.getTrim(), enderData.getPlayerName(), enderData.isLocked()).send2C(sender);
      }

      public static void send(Player player) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                  EnderInventory enderData = EnderStorage.getEnderData(player);
                  new SendEnderDisplay(player.getUUID(), enderData.getTrim(), enderData.getPlayerName(), enderData.isLocked()).send2A(server);
            }
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.ENDER_DISPLAY_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeNbt(trim);
            buf.writeComponent(playerName);
            buf.writeBoolean(locked);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.sendEnderData(uuid, in -> {
                  in.setTrim(trim);
                  in.setPlayerName(playerName);
                  in.setLocked(locked);
            });
      }
}
