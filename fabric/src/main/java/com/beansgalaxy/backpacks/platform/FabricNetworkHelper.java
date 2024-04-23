package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.*;
import com.beansgalaxy.backpacks.network.serverbound.*;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

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
      public void openBackpackMenu(Player viewer, Player owner) {
            viewer.openMenu(BackData.get(owner).backpackInventory.getMenuProvider());

      }

      @Override
      public MenuProvider getMenuProvider(Entity entity) {
            return new ExtendedScreenHandlerFactory() {

                  @Override
                  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeInt(entity.getId());
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
            };
      }
}
