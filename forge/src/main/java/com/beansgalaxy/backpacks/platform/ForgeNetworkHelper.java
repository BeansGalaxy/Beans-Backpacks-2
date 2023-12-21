package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacketC2S;
import com.beansgalaxy.backpacks.network.packages.SyncViewersPacketS2All;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class ForgeNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            NetworkPackages.C2S(new SprintKeyPacketC2S(sprintKeyPressed));
      }

      @Override
      public void SyncViewers(Entity owner, byte viewers) {
            int id = owner.getId();
            NetworkPackages.S2All(new SyncViewersPacketS2All(id, viewers));
      }

      @Override
      public void openBackpackMenu(Player player, Backpack entity) {
            if (player instanceof ServerPlayer serverPlayer)
                  serverPlayer.openMenu(entity.menuProvider, buf -> buf.writeInt(entity.getId()));
      }

      @Override
      public MenuProvider getMenuProvider(Backpack backpack) {
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
                              BackpackInventory backpackInventory = backpack.getBackpackInventory();
                              backpackInventory.addViewer(player);
                              return new BackpackMenu(containerId, player.getInventory(), backpackInventory);
                        }
                  }
            };
      }
}
