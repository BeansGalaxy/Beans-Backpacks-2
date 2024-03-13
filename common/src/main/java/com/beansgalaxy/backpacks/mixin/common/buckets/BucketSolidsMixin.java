package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SolidBucketItem.class)
public abstract class BucketSolidsMixin extends BlockItem implements BucketsAccess {
      @Shadow @Final private SoundEvent placeSound;

      public BucketSolidsMixin(Block $$0, Properties $$1) {
            super($$0, $$1);
      }

      @Override
      public Optional<SoundEvent> getPickupSound() {
            if (getBlock() instanceof BucketPickup pickup)
                  return pickup.getPickupSound();
            return Optional.empty();
      }

      @Override
      public SoundEvent defaultPlaceSound() {
            return placeSound;
      }

      @Override
      public Optional<BlockState> getBlockState() {
            return Optional.of(getBlock().defaultBlockState());
      }

      @Override
      public int scale() {
            return 4;
      }

      @Override
      public @NotNull Item getEmptyInstance() {
            return Items.BUCKET.asItem();
      }
}
