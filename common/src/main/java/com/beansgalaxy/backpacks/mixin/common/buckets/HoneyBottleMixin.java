package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleMixin implements BucketLikeAccess {

      @Override
      public Optional<BlockState> getBlockState() {
            return Optional.of(Blocks.HONEY_BLOCK.defaultBlockState());
      }

      @Override
      public Optional<SoundEvent> getPickupSound() {
            return Optional.of(SoundEvents.BOTTLE_FILL);
      }

      @Override
      public SoundEvent defaultPlaceSound() {
            return SoundEvents.BOTTLE_EMPTY;
      }

      @Override
      public boolean onPickup(Level level, BlockPos blockPos, BlockState blockState, Player player) {
            return true;
      }

      @Override
      public Optional<SoundEvent> uniquePlaceSound() {
            return Optional.of(SoundEvents.HONEY_BLOCK_PLACE);
      }

      @Override
      public int scale() {
            return 1;
      }

      @Override
      public @NotNull Item getEmptyInstance() {
            return Items.GLASS_BOTTLE.asItem();
      }

      @Override
      public Item getFilledInstance() {
            return Items.HONEY_BOTTLE.asItem();
      }

      @Override
      public int fullScale() {
            return 4;
      }
}
