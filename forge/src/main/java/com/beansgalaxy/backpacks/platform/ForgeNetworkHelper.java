package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.network.clientbound.ReceiveEnderPos;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderData;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackInventory;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import com.beansgalaxy.backpacks.network.clientbound.SyncViewers;
import com.beansgalaxy.backpacks.network.serverbound.*;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
      public void SprintKey2S(boolean sprintKeyPressed) {
            NetworkPackages.C2S(new SprintKey(sprintKeyPressed));
      }

      @Override
      public void SyncViewers2All(Entity owner, byte viewers) {
            int id = owner.getId();
            NetworkPackages.S2All(new SyncViewers(id, viewers));
      }

      @Override
      public void syncBackSlot2C(Player owner, @Nullable ServerPlayer sender) {
            if (sender == null)
                  NetworkPackages.S2All(new SyncBackSlot(owner.getUUID(), BackData.get(owner).getStack()));
            else
                  NetworkPackages.S2C(new SyncBackSlot(owner.getUUID(), BackData.get(owner).getStack()), sender);
      }

      @Override
      public void backpackInventory2C(ServerPlayer owner) {
            BackData backData = BackData.get(owner);
            ItemStack backStack = backData.getStack();
            if (backStack.getItem() instanceof EnderBackpack backpack) {
                  if (!backStack.isEmpty())
                        Services.NETWORK.sendEnderData2C(owner, backpack.getOrCreateUUID(owner.getUUID(), backStack));
                  return;
            }
            NetworkPackages.S2C(new SyncBackInventory(backData.backpackInventory), owner);
      }

      @Override
      public void instantPlace2S(int i, BlockHitResult blockHitResult) {
            NetworkPackages.C2S(new InstantPlace(i, blockHitResult));
      }

      @Override
      public void pickFromBackpack2S(int slot) {
            NetworkPackages.C2S(new PickBackpack(slot));
      }

      @Override
      public void sendEnderData2C(ServerPlayer player, UUID uuid) {
            NetworkPackages.S2C(new SendEnderData(uuid, EnderStorage.getEnderData(uuid, player.level())), player);
      }

      @Override
      public void sendEnderLocations2C(ServerPlayer serverPlayer, BackData backData) {
            NetworkPackages.S2C(new ReceiveEnderPos(backData), serverPlayer);
      }

      @Override
      public void useCauldron2S(BlockPos pos, UseKeyEvent.Type type) {
            NetworkPackages.C2S(new UseCauldron(pos, type));
      }

      @Override
      public void clearBackSlot2S(BackData backData) {
            NetworkPackages.C2S(new ClearBackSlot(backData));
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
