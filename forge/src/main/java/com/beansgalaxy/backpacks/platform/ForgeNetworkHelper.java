package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.*;
import com.beansgalaxy.backpacks.network.serverbound.*;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

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
                  Consumer<FriendlyByteBuf> consumer = owner instanceof EntityEnder ender ?
                        buf -> {
                              Optional<EnderInventory> enderData = ender.getEnderData();
                              enderData.ifPresentOrElse((ed) -> {
                                    buf.writeInt(-1);
                                    buf.writeUUID(ed.getUUID());
                              }, () -> {
                                    UUID uuid = viewer.getUUID();
                                    ender.setPlacedBy(Optional.of(uuid));
                                    buf.writeInt(-1);
                                    buf.writeUUID(uuid);
                              });
                        } : buf -> buf.writeInt(owner.getId());

                  NetworkHooks.openScreen(serverPlayer, owner.getInventory().getMenuProvider(), consumer);
            }
      }

      @Override
      public void openBackpackMenu(Player viewer, BackData backData) {
            if (viewer instanceof ServerPlayer serverPlayer) {
                  ItemStack stack = backData.getStack();
                  Consumer<FriendlyByteBuf> consumer = stack.getItem() instanceof EnderBackpack ender ?
                        buf -> {
                              UUID uuid = ender.getOrCreateUUID(viewer, stack);
                              buf.writeInt(-1);
                              buf.writeUUID(uuid);
                        } : buf -> buf.writeInt(backData.owner.getId());

                  NetworkHooks.openScreen(serverPlayer, backData.getBackpackInventory().getMenuProvider(), consumer);
            }
      }

      @Override
      public MenuProvider getMenuProvider(EntityAccess entity) {
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
