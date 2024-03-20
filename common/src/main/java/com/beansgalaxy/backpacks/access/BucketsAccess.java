package com.beansgalaxy.backpacks.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BucketsAccess {
      Optional<BlockState> getBlockState();

      Optional<SoundEvent> getPickupSound();

      default SoundEvent getPlaceSound() {
            return defaultPlaceSound();
      }

      SoundEvent defaultPlaceSound();

      int scale();

      @NotNull
      Item getEmptyInstance();

}
