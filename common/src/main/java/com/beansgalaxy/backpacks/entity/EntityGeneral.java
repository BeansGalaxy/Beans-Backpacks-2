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

import java.util.Optional;
import java.util.UUID;

public class EntityGeneral extends EntityAbstract {

      private final BackpackInventory inventory = new BackpackInventory() {

            public Entity getOwner() {
                  return EntityGeneral.this;
            }

            @Override
            public Traits.LocalData getTraits() {
                  return EntityGeneral.this.getTraits();
            }

            @Override
            public UUID getPlacedBy() {
                  return EntityGeneral.this.getPlacedBy();
            }

            @Override
            public Level level() {
                  return EntityGeneral.this.level();
            }

            @Override
            public void setChanged() {
                  EntityGeneral.this.level().updateNeighbourForOutputSignal(EntityGeneral.this.pos, Blocks.AIR);
                  super.setChanged();
            }

            @Override
            public boolean stopHopper() {
                  return isLocked();
            }
      };

      public EntityGeneral(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public EntityGeneral(Level level, NonNullList<ItemStack> stacks) {
            super(Services.REGISTRY.getGeneralEntity(), level);

            if (stacks != null && !stacks.isEmpty()) {
                  getInventory().getItemStacks().addAll(stacks);
                  stacks.clear();
            }
      }

      @Override
      public BackpackInventory getInventory() {
            return inventory;
      }

      @Override
      public boolean isCustomNameVisible() {
            return hasCustomName();
      }

      @Override
      public int getAnalogOutput() {
            return super.getAnalogOutput();
      }
}
