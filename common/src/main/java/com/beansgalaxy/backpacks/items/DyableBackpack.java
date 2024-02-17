package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public class DyableBackpack extends BackpackItem implements DyeableLeatherItem {

      @Override
      public InteractionResult useOn(UseOnContext ctx) {
            Level level = ctx.getLevel();
            BlockPos blockPos = ctx.getClickedPos();
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (block instanceof LayeredCauldronBlock)
            {
                  LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
                  clearColor(ctx.getItemInHand());
                  return InteractionResult.SUCCESS;
            } else
                  return super.useOn(ctx);
      }

      @Override
      public int getColor(ItemStack stack) {
            CompoundTag nbtCompound = stack.getTagElement(TAG_DISPLAY);
            if (nbtCompound != null && nbtCompound.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC)) {
                  return nbtCompound.getInt(TAG_COLOR);
            }
            return Backpack.DEFAULT_COLOR;
      }
}
