package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BucketItem.class)
public class BucketItemMixin implements BucketItemAccess {
      @Shadow @Final private Fluid content;

      @Override
      public Fluid beans_Backpacks_2$getFluid() {
            return content;
      }

      @Override
      public Optional<BlockState> getBlockState() {
            return Optional.of(content.defaultFluidState().createLegacyBlock());
      }

      @Override
      public Optional<SoundEvent> getPickupSound() {
            return content.getPickupSound();
      }

      @Override
      public SoundEvent defaultPlaceSound() {
            return content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
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
