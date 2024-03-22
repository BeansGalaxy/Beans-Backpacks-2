package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

public class DyableBackpack extends BackpackItem implements DyeableLeatherItem {

      public DyableBackpack(Item.Properties properties) {
            super(properties);
      }

      public static Color shiftColor(int colorInt) {
            Color base = new Color(Backpack.DEFAULT_COLOR);
            if (colorInt == Backpack.DEFAULT_COLOR)
                  return base;

            Color tint = new Color(colorInt);
            return weightedShift(base, tint, 4, 4, 3, 10);
      }

      public static Color weightedShift(Color secondary, Color primary, float red, float green, float blue, int value) {
            float r = (red * primary.getRed() + secondary.getRed()) / (red + 1);
            float g = (green * primary.getGreen() + secondary.getGreen()) / (green + 1);
            float b = (blue * primary.getBlue() + secondary.getBlue()) / (blue + 1);

            r += value;
            g += value;
            b += value;

            return new Color(Math.min((int) r, 255), Math.min((int) g, 255), Math.min((int) b, 255));
      }

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
