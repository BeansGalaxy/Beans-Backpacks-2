package com.beansgalaxy.backpacks.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public interface BucketsAccess {
      Optional<BlockState> getBlockState();

      Optional<SoundEvent> getPickupSound();

      SoundEvent getPlaceSound();
}
