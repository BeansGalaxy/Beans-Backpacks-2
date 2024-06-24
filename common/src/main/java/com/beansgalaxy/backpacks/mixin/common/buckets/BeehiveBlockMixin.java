package com.beansgalaxy.backpacks.mixin.common.buckets;

import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BaseEntityBlock implements BucketLikeAccess {
      @Shadow protected abstract boolean hiveContainsBees(Level level, BlockPos blockPos);
      @Shadow protected abstract void angerNearbyBees(Level level, BlockPos blockPos);
      @Shadow public abstract void releaseBeesAndResetHoneyLevel(Level level, BlockState blockState, BlockPos blockPos, @Nullable Player player, BeehiveBlockEntity.BeeReleaseStatus beeReleaseStatus);
      @Shadow public abstract void resetHoneyLevel(Level level, BlockState blockState, BlockPos blockPos);

      protected BeehiveBlockMixin(Properties $$0) {
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
            if (!CampfireBlock.isSmokeyPos(level, blockPos)) {
                  if (hiveContainsBees(level, blockPos)) {
                        angerNearbyBees(level, blockPos);
                  }

                  releaseBeesAndResetHoneyLevel(level, blockState, blockPos, player, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            } else {
                  resetHoneyLevel(level, blockState, blockPos);
            }

            if (player instanceof ServerPlayer serverPlayer) Services.REGISTRY.triggerSpecial(serverPlayer, SpecialCriterion.Special.CAULDRON_BEE_HIVE);
            return true;
      }

      @Override
      public SoundEvent defaultPlaceSound() {
            return SoundEvents.HONEY_BLOCK_PLACE;
      }

      @Override
      public int scale() {
            return 1;
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
