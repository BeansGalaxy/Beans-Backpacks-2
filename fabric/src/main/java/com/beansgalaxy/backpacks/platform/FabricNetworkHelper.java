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
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
      public void SprintKey2S(boolean sprintKeyPressed) {
            NetworkPackages.send(Network2S.SPRINT_KEY_2S, new SprintKey(sprintKeyPressed));
      }

      @Override
      public void SyncViewers2All(Entity owner, byte viewers) {
            Level world = owner.level();
            List<ServerPlayer> playerList = world.getServer().getPlayerList().getPlayers();
            for (ServerPlayer serverPlayer : playerList)
                  if (serverPlayer.level().dimension() == world.dimension())
                        NetworkPackages.send(Network2C.SYNC_VIEWERS_2C, new SyncViewers(owner.getId(), viewers), serverPlayer);
      }

      @Override
      public void syncBackSlot2C(Player owner, @Nullable ServerPlayer sender) {
            ItemStack stack = BackData.get(owner).getStack();
            if (sender == null) {
                  Level world = owner.level();
                  List<ServerPlayer> playerList = world.getServer().getPlayerList().getPlayers();
                  for (ServerPlayer serverPlayer : playerList)
                        NetworkPackages.send(Network2C.SYNC_BACK_SLOT_2C, new SyncBackSlot(owner.getUUID(), stack), serverPlayer);
            }
            else NetworkPackages.send(Network2C.SYNC_BACK_SLOT_2C, new SyncBackSlot(owner.getUUID(), stack), sender);
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
            NetworkPackages.send(Network2C.SYNC_BACK_INV_2C, new SyncBackInventory(backData.backpackInventory), owner);
      }

      @Override
      public void instantPlace2S(int i, BlockHitResult blockHitResult) {
            NetworkPackages.send(Network2S.INSTANT_PLACE_2S, new InstantPlace(i, blockHitResult));
      }

      @Override
      public void pickFromBackpack2S(int slot) {
            NetworkPackages.send(Network2S.PICK_BACKPACK_2S, new PickBackpack(slot));
      }

      @Override
      public void sendEnderData2C(ServerPlayer player, UUID uuid) {
            NetworkPackages.send(Network2C.SEND_ENDER_DATA_2C, new SendEnderData(uuid, EnderStorage.getEnderData(uuid, player.level())), player);
      }

      @Override
      public void sendEnderLocations2C(ServerPlayer serverPlayer, BackData backData) {
            NetworkPackages.send(Network2C.ENDER_POS_2C, new ReceiveEnderPos(backData), serverPlayer);
      }

      @Override
      public void useCauldron2S(BlockPos pos, UseKeyEvent.Type type) {
            NetworkPackages.send(Network2S.USE_CAULDRON_2S, new UseCauldron(pos, type));
      }

      @Override
      public void clearBackSlot2S(BackData backData) {
            NetworkPackages.send(Network2S.CLEAR_BACK_SLOT_2S, new ClearBackSlot(backData));
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
