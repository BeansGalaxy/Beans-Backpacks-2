package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.client.SyncBackInventory2C;
import com.beansgalaxy.backpacks.network.client.SyncBackSlotS2C;
import com.beansgalaxy.backpacks.network.client.SyncViewersPacketS2C;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacketC2S;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.nbt.CompoundTag;
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
            NetworkPackages.S2All(new SyncViewersPacketS2C(id, viewers));
      }

      @Override
      public void openBackpackMenu(Player viewer, Backpack owner) {
            if (viewer instanceof ServerPlayer serverPlayer) {
                  serverPlayer.openMenu(owner.getBackpackInventory().getMenuProvider(), buf -> buf.writeInt(owner.getId()));
            }
      }

      @Override
      public void openBackpackMenu(Player viewer, Player owner) {
            if (viewer instanceof ServerPlayer serverPlayer)
                  serverPlayer.openMenu(BackSlot.getInventory(owner).getMenuProvider(), buf -> buf.writeInt(owner.getId()));
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
                              backpackInventory.addViewer(player);
                              return new BackpackMenu(containerId, player.getInventory(), backpackInventory);
                        }
                  }
            };
      }

      @Override
      public void SyncBackSlot(ServerPlayer owner) {
            NetworkPackages.S2All(new SyncBackSlotS2C(owner.getUUID(), BackSlot.get(owner).getItem()));
      }

      @Override
      public void backpackInventory2C(ServerPlayer owner) {
            BackpackInventory backpackInventory = BackSlot.getInventory(owner);
            CompoundTag compound = new CompoundTag();
            backpackInventory.writeNbt(compound, backpackInventory.isEmpty());
            String stacks = compound.getAsString();
            NetworkPackages.S2C(new SyncBackInventory2C(stacks), owner);
      }
}
