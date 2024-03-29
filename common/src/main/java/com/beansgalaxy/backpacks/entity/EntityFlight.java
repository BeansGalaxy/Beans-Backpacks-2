package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

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
            public Traits.LocalData getTraits() {
                  return EntityFlight.this.getTraits();
            }

            @Override
            public UUID getPlacedBy() {
                  return EntityFlight.this.getPlacedBy();
            }

            @Override
            public void setChanged() {
                  EntityFlight.this.level().updateNeighbourForOutputSignal(EntityFlight.this.pos, Blocks.AIR);
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
            this.entityData.set(OWNER, Optional.of(player.getUUID()));

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

      @Override
      public AABB getBoundingBoxForCulling() {
            return getBoundingBox().inflate(2, 0, 2);
      }
}
