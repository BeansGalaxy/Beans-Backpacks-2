package com.beansgalaxy.backpacks.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

import java.util.Optional;

public interface BucketLikeAccess extends BucketsAccess {
      default SoundEvent getPlaceSound() {
            return uniquePlaceSound().orElseGet(this::defaultPlaceSound);
      }

      default Optional<SoundEvent> uniquePlaceSound() {
            return Optional.empty();
      }

      Item getFilledInstance();

      int fullScale();

}
