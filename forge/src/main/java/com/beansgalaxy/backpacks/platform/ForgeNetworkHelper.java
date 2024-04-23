package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.*;
import com.beansgalaxy.backpacks.network.serverbound.*;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ForgeNetworkHelper implements NetworkHelper {
      @Override
      public void send(Network2S network, Packet2S msg) {
            NetworkPackages.C2S(msg);
      }

      @Override
      public void send(Network2C network, Packet2C msg, ServerPlayer sender) {
            NetworkPackages.S2C(msg, sender);
      }

      @Override
      public void send(Network2C network2C, Packet2C msg, MinecraftServer server) {
            NetworkPackages.S2All(msg);
      }

      @Override
      public void openBackpackMenu(Player viewer, EntityAbstract owner) {
            if (viewer instanceof ServerPlayer serverPlayer) {
                  NetworkHooks.openScreen(serverPlayer, owner.getInventory().getMenuProvider(), buf -> buf.writeInt(owner.getId()));
            }
      }

      @Override
      public void openBackpackMenu(Player viewer, Player owner) {
            if (viewer instanceof ServerPlayer serverPlayer)
                  NetworkHooks.openScreen(serverPlayer, BackData.get(owner).backpackInventory.getMenuProvider(), buf -> buf.writeInt(owner.getId()));
      }

      @Override
      public MenuProvider getMenuProvider(Entity entity) {
            return new MenuProvider() {

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
