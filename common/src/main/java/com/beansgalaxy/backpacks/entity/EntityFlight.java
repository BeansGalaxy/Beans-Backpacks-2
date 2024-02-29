package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class EntityFlight extends EntityAbstract {

      private final BackpackInventory inventory = new BackpackInventory() {

            public Entity getOwner() {
                  return EntityFlight.this;
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
                  return EntityFlight.this.getItemStacks();
            }

            @Override
            public Traits.LocalData getLocalData() {
                  return EntityFlight.this.getLocalData();
            }

            @Override
            public UUID getPlacedBy() {
                  return EntityFlight.this.getPlacedBy();
            }

            @Override
            public void setChanged() {
                  BackpackInventory.super.setChanged();
            }
      };

      private final int damage;

      public EntityFlight(EntityType<? extends Entity> type, Level level) {
            super(type, level);
            this.damage = 0;
      }

      public EntityFlight(Player player, NonNullList<ItemStack> stacks, int damage) {
            super(Services.REGISTRY.getWingedEntity(), player.level());
            this.damage = damage;
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
      public int getDamage() {
            return damage;
      }

      @Override
      public boolean isCustomNameVisible() {
            return hasCustomName();
      }
}
