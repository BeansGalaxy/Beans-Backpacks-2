package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.packages.UseCauldron2S;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.client.*;
import com.beansgalaxy.backpacks.network.packages.InstantPlace2S;
import com.beansgalaxy.backpacks.network.packages.PickBackpack2S;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket2S;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ForgeNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            NetworkPackages.C2S(new SprintKeyPacket2S(sprintKeyPressed));
      }

      @Override
      public void SyncViewers(Entity owner, byte viewers) {
            int id = owner.getId();
            NetworkPackages.S2All(new SyncViewersPacket2C(id, viewers));
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

      @Override
      public void SyncBackSlot(ServerPlayer owner) {
            NetworkPackages.S2All(new SyncBackSlot2C(owner.getUUID(), BackData.get(owner).getStack()));
      }

      @Override
      public void backpackInventory2C(ServerPlayer owner) {
            BackpackInventory backpackInventory = BackData.get(owner).backpackInventory;
            CompoundTag compound = new CompoundTag();
            backpackInventory.writeNbt(compound);
            String stacks = compound.getAsString();
            NetworkPackages.S2C(new SyncBackInventory2C(stacks), owner);
      }

      @Override
      public void instantPlace(int i, BlockHitResult blockHitResult) {
            NetworkPackages.C2S(new InstantPlace2S(i, blockHitResult));
      }

      @Override
      public void pickFromBackpack2S(int slot) {
            NetworkPackages.C2S(new PickBackpack2S(slot));
      }

      @Override
      public void sendEnderData2C(ServerPlayer player, UUID uuid) {
            NetworkPackages.S2C(new SendEnderData2C(uuid, EnderStorage.getEnderData(uuid, player.level())), player);
      }

      @Override
      public void sendEnderLocations2C(ServerPlayer serverPlayer, BackData backData) {
            NetworkPackages.S2C(new ReceiveEnderPos(backData.getEnderLocations()), serverPlayer);
      }

      @Override
      public void useCauldron2S(BlockPos pos, boolean isPlace) {
            NetworkPackages.C2S(new UseCauldron2S(pos, isPlace));
      }
}
