package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class EntityGeneral extends EntityAbstract {

      private final BackpackInventory inventory = new BackpackInventory() {

            public Entity getOwner() {
                  return EntityGeneral.this;
            }

            NonNullList<ServerPlayer> playersViewing = NonNullList.create();

            @Override
            public Viewable getViewable() {
                  return viewable;
            }

            @Override
            public NonNullList<ServerPlayer> getPlayersViewing() {
                  return playersViewing;
            }

            @Override
            public NonNullList<ItemStack> getItemStacks() {
                  return EntityGeneral.this.getItemStacks();
            }

            @Override
            public Traits.LocalData getLocalData() {
                  return EntityGeneral.this.getLocalData();
            }

            @Override
            public UUID getPlacedBy() {
                  return EntityGeneral.this.getPlacedBy();
            }

            @Override
            public void setChanged() {
                  BackpackInventory.super.setChanged();
            }
      };

      public EntityGeneral(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public EntityGeneral(Player player, NonNullList<ItemStack> stacks) {
            super(Services.REGISTRY.getGeneralEntity(), player.level());
            this.entityData.set(PLACED_BY, Optional.of(player.getUUID()));

            if (stacks != null && !stacks.isEmpty()) {
                  getItemStacks().addAll(stacks);
                  stacks.clear();
            }
      }

      @Override
      public BackpackInventory getInventory() {
            return inventory;
      }

      protected NonNullList<ItemStack> itemStacks = NonNullList.create();

      @Override
      NonNullList<ItemStack> getItemStacks() {
            return itemStacks;
      }

      @Override
      public boolean isCustomNameVisible() {
            return hasCustomName();
      }
}