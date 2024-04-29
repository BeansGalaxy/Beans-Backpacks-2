package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.*;
import com.beansgalaxy.backpacks.network.serverbound.*;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.entity.EntityAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricNetworkHelper implements NetworkHelper {

      @Override
      public void send(Network2S network, Packet2S msg) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            msg.encode(buf);
            ClientPlayNetworking.send(network.id, buf);
      }

      @Override
      public void send(Network2C network, Packet2C msg, ServerPlayer sender) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            msg.encode(buf);
            ServerPlayNetworking.send(sender, network.id, buf);
      }

      @Override
      public void send(Network2C network, Packet2C msg, MinecraftServer server) {
            List<ServerPlayer> playerList = server.getPlayerList().getPlayers();
            for (ServerPlayer sender : playerList)
                  send(network, msg, sender);
      }

      @Override
      public void openBackpackMenu(Player viewer, EntityAbstract owner) {
            viewer.openMenu(owner.getInventory().getMenuProvider());
      }

      @Override
      public void openBackpackMenu(Player viewer, BackData owner) {
            viewer.openMenu(owner.getBackpackInventory().getMenuProvider());

      }

      @Override
      public MenuProvider getMenuProvider(EntityAccess entity) {
            return new ExtendedScreenHandlerFactory() {

                  @Override
                  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeInt(entity.getId());
                        buf.writeUUID(entity.getUUID());
                  }

                  @Override
                  public Component getDisplayName() {
                        return Component.literal("");
                  }

                  @Nullable
                  @Override
                  public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                        if (player.isSpectator())
                              return null;
                        else {
                              BackpackInventory backpackInventory = BackpackInventory.get(entity);
                              if (player instanceof ServerPlayer serverPlayer)
                                    backpackInventory.addViewer(serverPlayer);
                              return new BackpackMenu(containerId, player.getInventory(), backpackInventory);
                        }
                  }

                  @Override
                  public boolean shouldCloseCurrentScreen() {
                        return false;
                  }
            };
      }
}
