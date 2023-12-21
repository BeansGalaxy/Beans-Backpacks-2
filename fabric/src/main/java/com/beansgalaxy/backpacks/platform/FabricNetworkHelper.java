package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket;
import com.beansgalaxy.backpacks.network.packages.SyncViewersS2All;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FabricNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            SprintKeyPacket.C2S(sprintKeyPressed);
      }

      @Override
      public void SyncViewers(Entity owner, byte viewers) {
            SyncViewersS2All.S2C(owner, viewers);
      }

      @Override
      public void openBackpackMenu(Player player, Backpack entity) {
            player.openMenu(entity.menuProvider);
      }

      @Override
      public MenuProvider getMenuProvider(Backpack backpack) {
            return new ExtendedScreenHandlerFactory() {

                  @Override
                  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeInt(backpack.getId());
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
                              BackpackInventory backpackInventory = backpack.getBackpackInventory();
                              backpackInventory.addViewer(player);
                              return new BackpackMenu(containerId, player.getInventory(), backpackInventory);
                        }
                  }
            };
      }
}
