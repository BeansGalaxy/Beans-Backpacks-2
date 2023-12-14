package com.beansgalaxy.backpacks.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class DyableBackpack extends BackpackItem implements DyeableLeatherItem {

      @Override
      public int getColor(ItemStack stack) {
            CompoundTag nbtCompound = stack.getTagElement(TAG_DISPLAY);
            if (nbtCompound != null && nbtCompound.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC)) {
                  return nbtCompound.getInt(TAG_COLOR);
            }
            return DEFAULT_COLOR;
      }
}
