package com.beansgalaxy.backpacks.access;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface BucketLikeAccess extends BucketsAccess {
      default SoundEvent getPlaceSound() {
            return uniquePlaceSound().orElseGet(this::defaultPlaceSound);
      }

      boolean onPickup(Level level, BlockPos blockPos, BlockState blockState, Player player);

      default Optional<SoundEvent> uniquePlaceSound() {
            return Optional.empty();
      }

      Item getFilledInstance();

      int fullScale();

}
