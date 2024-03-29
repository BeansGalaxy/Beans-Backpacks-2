package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(HoneyBlock.class)
public abstract class HoneyBlockMixin extends HalfTransparentBlock implements BucketLikeAccess {
      protected HoneyBlockMixin(Properties $$0) {
            super($$0);
      }

      @Override
      public Optional<BlockState> getBlockState() {
            return Optional.of(defaultBlockState());
      }

      @Override
      public Optional<SoundEvent> getPickupSound() {
            return Optional.of(SoundEvents.HONEY_BLOCK_BREAK);
      }

      @Override
      public boolean onPickup(Level level, BlockPos blockPos, BlockState blockState, Player player) {
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
            if (!level.isClientSide())
                  level.levelEvent(2001, blockPos, Block.getId(blockState));
            return true;
      }

      @Override
      public SoundEvent defaultPlaceSound() {
            return SoundEvents.HONEY_BLOCK_PLACE;
      }

      @Override
      public int scale() {
            return 4;
      }

      @Override
      public @NotNull Item getEmptyInstance() {
            return Items.AIR;
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
