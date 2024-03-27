package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class UseKeyEvent {
      public enum Type {
            PICKUP((byte) 0),
            PLACE((byte) 1),
            EQUIP((byte) 2);

            public final byte id;
            Type(byte id) {
                  this.id = id;
            }

            public static Type byID(byte id) {
                  for (Type value : Type.values()) {
                        if (value.id == id) {
                              return value;
                        }
                  }
                  return PICKUP;
            }
      }

      public static boolean cauldronPickup(Player player) {
            BackData backData = BackData.get(player);
            if (backData.actionKeyPressed && Kind.CAULDRON.is(backData.getStack())) {
                  Player owner = backData.owner;
                  Level level = owner.level();
                  BlockHitResult blockHitResult = getPlayerPOVHitResult(level, owner, ClipContext.Fluid.SOURCE_ONLY);
                  BlockPos blockPos = blockHitResult.getBlockPos();
                  if (blockHitResult.getType() == HitResult.Type.BLOCK && cauldronPickup(player, blockPos, level, backData)) {
                        if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, Type.PICKUP);
                        return true;
                  }
            }
            return false;
      }

      public static boolean cauldronPickup(BlockHitResult blockHitResult, Player player) {
            BackData backData = BackData.get(player);
            if (backData.actionKeyPressed && blockHitResult.getType() == HitResult.Type.BLOCK) {
                  Player owner = backData.owner;
                  Level level = owner.level();
                  BlockPos blockPos = blockHitResult.getBlockPos();
                  if (potCauldronEquip(blockPos, level, backData)) {
                        if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, Type.EQUIP);
                        return true;
                  }
                  else if (Kind.CAULDRON.is(backData.getTraits().kind) && cauldronPickup(player, blockPos, level, backData))
                  {
                        if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, Type.PICKUP);
                        return true;
                  }
            }
            return false;
      }

      public static boolean potCauldronEquip(BlockPos blockPos, Level level, BackData backData) {
            ItemStack backStack = backData.getStack();
            if (!backStack.isEmpty())
                  return false;

            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            Player player = backData.owner;
            BlockEntity blockEntity = level.getChunkAt(blockPos).getBlockEntity(blockPos, LevelChunk.EntityCreationType.IMMEDIATE);
            if (blockEntity instanceof DecoratedPotBlockEntity potBlock) {
                  DecoratedPotBlockEntity.Decorations decorations = potBlock.getDecorations();
                  ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
                  CompoundTag compoundtag = decorations.save(new CompoundTag());
                  BlockItem.setBlockEntityData(itemstack, BlockEntityType.DECORATED_POT, compoundtag);
                  backData.set(itemstack);
                  if (!level.isClientSide()) {
                        level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                        level.gameEvent(null, GameEvent.BLOCK_DESTROY, blockPos);
                  }
                  level.playSound(player, blockPos, SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1f, 1f);
                  player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC);
                  return true;
            }
            else if (block instanceof AbstractCauldronBlock cauldronBlock)
            {
                  ItemStack cauldron = Items.CAULDRON.getDefaultInstance();
                  backData.set(cauldron);
                  int analogOutputSignal = cauldronBlock.getAnalogOutputSignal(blockState, level, blockPos);
                  if (analogOutputSignal == 3) {
                        if (block.equals(Blocks.WATER_CAULDRON)) {
                              CauldronInventory.add(cauldron, Items.WATER_BUCKET);
                        }
                        else if (block.equals(Blocks.LAVA_CAULDRON)) {
                              CauldronInventory.add(cauldron, Items.LAVA_BUCKET);
                        }
                        else if (block.equals(Blocks.POWDER_SNOW_CAULDRON)) {
                              CauldronInventory.add(cauldron, Items.POWDER_SNOW_BUCKET);
                        }
                  }
                  if (!level.isClientSide()) {
                        level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                        level.gameEvent(null, GameEvent.BLOCK_DESTROY, blockPos);
                  }
                  level.playSound(player, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f, 1f);
                  player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC);
                  return true;
            }

            return false;
      }

      public static boolean cauldronPickup(Player player, BlockPos blockPos, Level level, BackData backData) {
            BlockState blockState = level.getBlockState(blockPos);
            ItemStack cauldron = backData.getStack();
            if (CauldronInventory.sizeLeft(cauldron) <= 0) return false;

            Block block = blockState.getBlock();
            if (block instanceof LiquidBlock liquidBlock) {
                  FluidState fluidState = liquidBlock.getFluidState(blockState);
                  if (fluidState.isSource()) {
                        Fluid type = fluidState.getType();
                        if (CauldronInventory.add(cauldron, type.getBucket()) != null) {
                              type.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                              level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                              level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
                              return true;
                        }
                  }
            }
            else if (block instanceof SimpleWaterloggedBlock && blockState.getValue(BlockStateProperties.WATERLOGGED) && CauldronInventory.add(cauldron, Items.WATER_BUCKET) != null) {
                  Fluids.WATER.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                  level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                  level.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, false), 3);
                  if (!blockState.canSurvive(level, blockPos))
                        level.destroyBlock(blockPos, true);
                  return true;
            }
            else if (block instanceof BucketPickup pickup) {
                  Item bucket = CauldronInventory.getBucket(cauldron);
                  boolean shouldPickup = false;
                  if (bucket instanceof BucketsAccess access) {
                        Optional<BlockState> optional = access.getBlockState();
                        if (optional.isPresent() && optional.get().is(blockState.getBlock()))
                              shouldPickup = true;
                  }
                  else if (bucket.equals(Items.AIR))
                        shouldPickup = true;

                  if (shouldPickup) {
                        ItemStack stack = pickup.pickupBlock(level, blockPos, blockState);
                        if (!stack.isEmpty()) {
                              Item item = stack.getItem();
                              if (item instanceof BucketsAccess access) access.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                              CauldronInventory.add(cauldron, item);
                              return true;
                        }
                  }
            }
            else if (block instanceof AbstractCauldronBlock cauldronBlock) {
                  int analogOutputSignal = cauldronBlock.getAnalogOutputSignal(blockState, level, blockPos);
                  if (analogOutputSignal == 3) {
                        BucketsAccess bucket = null;
                        if (block.equals(Blocks.WATER_CAULDRON) && CauldronInventory.add(cauldron, Items.WATER_BUCKET) != null)
                              bucket = (BucketsAccess) Items.WATER_BUCKET;
                        if (block.equals(Blocks.LAVA_CAULDRON) && CauldronInventory.add(cauldron, Items.LAVA_BUCKET) != null)
                              bucket = (BucketsAccess) Items.LAVA_BUCKET;
                        if (block.equals(Blocks.POWDER_SNOW_CAULDRON) && CauldronInventory.add(cauldron, Items.POWDER_SNOW_BUCKET) != null)
                              bucket = (BucketsAccess) Items.POWDER_SNOW_BUCKET;
                        if (bucket != null) {
                              level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), 11);
                              if (!level.isClientSide()) {
                                    player.awardStat(Stats.USE_CAULDRON);
                                    level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
                                    bucket.getPickupSound().ifPresent(soundEvent ->
                                                level.playSound(null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
                                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                              }
                              return true;
                        }
                  }
            }
            else if (block instanceof BucketLikeAccess blockAccess) {
                  Item bucket = CauldronInventory.getBucket(cauldron);
                  boolean shouldPickup = false;
                  if (blockAccess.getFilledInstance().equals(bucket))
                        shouldPickup = true;
                  else if (bucket.equals(Items.AIR))
                        shouldPickup = true;

                  if (shouldPickup && blockAccess.onPickup(level, blockPos, blockState, player)) {
                        blockAccess.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1, 1));
                        CauldronInventory.add(cauldron, blockAccess);
                        return true;
                  }
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
                  Item bucket = CauldronInventory.getBucket(cauldron);
                  if (bucket.equals(Items.AIR)) return false;

                  Level level = player.level();
                  BlockPos blockPos = blockHitResult.getBlockPos();
                  BlockState blockState = level.getBlockState(blockPos);

                  boolean replaceable = bucket instanceof BucketItemAccess access && blockState.canBeReplaced(access.beans_Backpacks_2$getFluid());
                  if (!(replaceable || blockState.canBeReplaced()) && !(blockState.getBlock() instanceof LiquidBlockContainer)) {
                        blockPos = blockPos.relative(blockHitResult.getDirection());
                        blockState = level.getBlockState(blockPos);
                  }

                  if (cauldronPlace(level, blockPos, blockState, backData)) {
                        if (level.isClientSide()) Services.NETWORK.useCauldron2S(blockPos, Type.PLACE);
                        return true;
                  }
            }
            return false;
      }

      public static boolean cauldronPlace(Level level, BlockPos blockPos, BlockState blockState, BackData backData) {
            ItemStack cauldron = backData.getStack();
            CauldronInventory.Attributes attributes = CauldronInventory.Attributes.create(cauldron);
            if (attributes != null && attributes.amount() >= attributes.amountToPlace()) {
                  BucketsAccess bucketAccess = attributes.access();
                  Item bucket = attributes.bucket();
                  Player player = backData.owner;
                  Block block = blockState.getBlock();
                  if (bucket instanceof BucketItemAccess access &&
                              block instanceof LiquidBlockContainer liquidContainer &&
                              liquidContainer.canPlaceLiquid(level, blockPos, blockState, access.beans_Backpacks_2$getFluid()) &&
                              liquidContainer.placeLiquid(level, blockPos, blockState, access.beans_Backpacks_2$getFluid().defaultFluidState())) {
                        playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                        return true;
                  }

                  if (level.dimensionType().ultraWarm() && bucket.equals(Items.WATER_BUCKET)) {
                        int $$8 = blockPos.getX();
                        int $$9 = blockPos.getY();
                        int $$10 = blockPos.getZ();
                        level.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

                        for (int $$11 = 0; $$11 < 8; ++$$11)
                              level.addParticle(ParticleTypes.LARGE_SMOKE, (double) $$8 + Math.random(), (double) $$9 + Math.random(), (double) $$10 + Math.random(), 0.0, 0.0, 0.0);

                        CauldronInventory.remove(cauldron);
                        return true;
                  }

                  FluidState blockFluidState = blockState.getFluidState();
                  if (blockState.canBeReplaced() && bucketAccess.getBlockState().isPresent()) {
                        if (!level.isClientSide() && !blockState.liquid())
                              level.destroyBlock(blockPos, true);
                        if (level.setBlock(blockPos, bucketAccess.getBlockState().get(), 11)
                        || blockFluidState.getType().getBucket().equals(bucket) && blockFluidState.isSource()) {
                              if (bucketAccess instanceof BucketLikeAccess access)
                                    playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron, access));
                              else
                                    playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                        }
                        return true;

                  }

                  if (Items.WATER_BUCKET.equals(bucket) && block instanceof SimpleWaterloggedBlock) {
                        playEmptySound(player, level, blockPos, CauldronInventory.remove(cauldron));
                        return true;
                  }
            }
            return false;
      }

      protected static void playEmptySound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2, Item bucket) {
            SoundEvent $$3 = bucket instanceof BucketsAccess access ? access.getPlaceSound() : SoundEvents.BUCKET_EMPTY;
            $$1.playSound($$0, $$2, $$3, SoundSource.BLOCKS, 1.0F, 1.0F);
            $$1.gameEvent($$0, GameEvent.FLUID_PLACE, $$2);
      }
}
