package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.Traits;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class EnderEntity extends BackpackEntity {

      public EnderEntity(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public EnderEntity(Player player, int x, double y, int z, Direction direction, Traits.LocalData traits, float yaw, UUID uuid) {
            super(player);
            setupDisplay(player, x, y, z, direction, traits, yaw);
            itemStacks = ServerSave.getEnderData(getPlacedBy(), level()).getItemStacks();
            entityData.set(PLACED_BY, Optional.of(uuid));
      }

      @Override
      public CompoundTag getTrim() {
            return ServerSave.getEnderData(getPlacedBy(), level()).getTrim();
      }

      @Override
      protected NonNullList<ItemStack> getItemStacks() {
            return ServerSave.getEnderData(getPlacedBy(), level()).getItemStacks();
      }

      @Override
      public void setChanged() {

      }
}
