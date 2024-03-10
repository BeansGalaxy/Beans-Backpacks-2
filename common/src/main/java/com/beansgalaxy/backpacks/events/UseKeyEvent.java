package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class UseKeyEvent {
      public static boolean cauldronPickup(Player player) {
            BackData backData = BackData.get(player);
            if (backData.actionKeyPressed && Kind.CAULDRON.is(backData.getStack())) {
                  Player owner = backData.owner;
                  Level level = owner.level();
                  BlockHitResult blockHitResult = getPlayerPOVHitResult(level, owner, ClipContext.Fluid.SOURCE_ONLY);
                  if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                        return cauldronPickup(player, blockHitResult.getBlockPos(), level, backData);
                  }
            }
            return false;
      }

      public static boolean cauldronPickup(Player player, BlockPos blockPos, Level level, BackData backData) {
            BlockState blockState = level.getBlockState(blockPos);
            ItemStack cauldron = backData.getStack();
            if (blockState.getBlock() instanceof LiquidBlock liquidBlock) {
                  FluidState fluidState = liquidBlock.getFluidState(blockState);
                  if (fluidState.isSource()) {
                        Fluid type = fluidState.getType();
                        if (CauldronInventory.add(cauldron, type)) {
                              if (level.isClientSide())
                                    Services.NETWORK.useCauldron2S(blockPos, false);
                              type.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                              level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                              level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
                              return true;
                        }
                  }
            }
            if (blockState.getBlock() instanceof SimpleWaterloggedBlock && blockState.getValue(BlockStateProperties.WATERLOGGED) && CauldronInventory.add(cauldron, Fluids.WATER)) {
                  if (level.isClientSide())
                        Services.NETWORK.useCauldron2S(blockPos, false);
                  Fluids.WATER.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                  level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                  level.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, false), 3);
                  if (!blockState.canSurvive(level, blockPos))
                        level.destroyBlock(blockPos, true);
                  return true;
            }
            return false;
      }

      protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
            float f = player.getXRot();
            float g = player.getYRot();
            Vec3 vec3 = player.getEyePosition();
            float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
            float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
            float j = -Mth.cos(-f * 0.017453292F);
            float k = Mth.sin(-f * 0.017453292F);
            float l = i * j;
            float n = h * j;
            double d = 5.0;
            Vec3 vec32 = vec3.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
            return level.clip(new ClipContext(vec3, vec32, net.minecraft.world.level.ClipContext.Block.OUTLINE, fluid, player));
      }

      public static boolean cauldronPlace(Player player, BlockHitResult blockHitResult) {
            if (blockHitResult.getType() == HitResult.Type.MISS) return false;

            BackData backData = BackData.get(player);
            ItemStack cauldron = backData.getStack();
            if (backData.actionKeyPressed && Kind.CAULDRON.is(cauldron)) {
                  Fluid fluid = CauldronInventory.get(cauldron);
                  if (fluid.isSame(Fluids.EMPTY)) return false;

                  Level level = player.level();
                  BlockPos blockPos = blockHitResult.getBlockPos();
                  BlockState blockState = level.getBlockState(blockPos);

                  if (!blockState.canBeReplaced(fluid) && !(blockState.getBlock() instanceof LiquidBlockContainer)) {
                        blockPos = blockPos.relative(blockHitResult.getDirection());
                        blockState = level.getBlockState(blockPos);
                  }

                  return cauldronPlace(level, blockPos, blockState, backData);
            }
            return false;
      }

      public static boolean cauldronPlace(Level level, BlockPos blockPos, BlockState blockState, BackData backData) {
            ItemStack cauldron = backData.getStack();
            Fluid fluid = CauldronInventory.get(cauldron);
            if (fluid.isSame(Fluids.EMPTY))
                  return false;

            Player player = backData.owner;
            if (blockState.getBlock() instanceof LiquidBlockContainer liquidContainer &&
                        liquidContainer.canPlaceLiquid(level, blockPos, blockState, fluid) &&
                        liquidContainer.placeLiquid(level, blockPos, blockState, fluid.defaultFluidState())) {
                  if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, true);
                  playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                  return true;
            }

            if (level.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
                  int $$8 = blockPos.getX();
                  int $$9 = blockPos.getY();
                  int $$10 = blockPos.getZ();
                  level.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

                  for (int $$11 = 0; $$11 < 8; ++$$11)
                        level.addParticle(ParticleTypes.LARGE_SMOKE, (double) $$8 + Math.random(), (double) $$9 + Math.random(), (double) $$10 + Math.random(), 0.0, 0.0, 0.0);

                  if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, true);
                  CauldronInventory.remove(cauldron);
                  return true;
            }

            FluidState blockFluidState = blockState.getFluidState();
            if (blockState.canBeReplaced(fluid)) {
                  if (!level.isClientSide && !blockState.liquid())
                        level.destroyBlock(blockPos, true);
                  if (level.setBlock(blockPos, fluid.defaultFluidState().createLegacyBlock(), 11) && !blockFluidState.isSource()) {
                        if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, true);
                        playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                        return true;
                  }
            }

            if ((blockFluidState.getType().isSame(fluid) && blockFluidState.isSource())
            || (Fluids.WATER.isSame(fluid) && blockState.getBlock() instanceof SimpleWaterloggedBlock)) {
                  if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, true);
                  playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                  return true;
            }

            return false;
      }

      protected static void playEmptySound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2, Fluid fluid) {
            SoundEvent $$3 = fluid.isSame(Fluids.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
            $$1.playSound($$0, $$2, $$3, SoundSource.BLOCKS, 1.0F, 1.0F);
            $$1.gameEvent($$0, GameEvent.FLUID_PLACE, $$2);
      }
}
