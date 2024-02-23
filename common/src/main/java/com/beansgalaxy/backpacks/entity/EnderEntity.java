package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.Traits;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnderEntity extends BackpackEntity {

      public EnderEntity(Player player, int x, double y, int z, Direction direction, Traits.LocalData traits, float yaw) {
            super(player);
            setupDisplay(player, x, y, z, direction, traits, yaw);
            itemStacks = ServerSave.getEnderData(placedBy).getItemStacks();
      }

      @Override
      public CompoundTag getTrim() {
            return ServerSave.getEnderData(placedBy).getTrim();
      }

      @Override
      protected NonNullList<ItemStack> getItemStacks() {
            return ServerSave.getEnderData(placedBy).getItemStacks();
      }

      @Override
      public void setChanged() {

      }
}
