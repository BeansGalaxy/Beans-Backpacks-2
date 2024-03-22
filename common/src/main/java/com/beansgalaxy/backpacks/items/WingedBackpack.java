package com.beansgalaxy.backpacks.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class WingedBackpack extends DyableBackpack {
    public static final int WINGED_ENTITY = 0x7F8F9F;
    public static final int WINGED_ITEM_0 = 0x8c8c8c;
    public static final int WINGED_ITEM_2 = 0x8f8fb3;

    public WingedBackpack(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.isDamaged();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float $$1 = Math.max(0.0F, ((float) this.getMaxDamage() - (float)stack.getDamageValue()) / (float) this.getMaxDamage());
        return Mth.hsvToRgb($$1 / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - stack.getDamageValue() * 13.0F / this.getMaxDamage());
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTagElement(TAG_DISPLAY);
        if (nbtCompound != null && nbtCompound.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC)) {
            return nbtCompound.getInt(TAG_COLOR);
        }
        return WINGED_ENTITY;
    }

    public static Color shiftColor(int colorInt) {
        Color winged = new Color(WINGED_ENTITY);
        if (colorInt == WINGED_ENTITY)
            return winged;

        Color color = new Color(colorInt);
        return weightedShift(winged, color, 3, 3, 2, 20);
    }

    public static int shiftLayer0(int colorInt) {
        if (colorInt == WINGED_ENTITY)
            return WINGED_ITEM_0;

        Color newColor = weightedShift(new Color(colorInt), new Color(WINGED_ITEM_0), 1.5f, 1.5f, 1.5f, 10);

        int r = newColor.getRed();
        int g = newColor.getGreen();
        int b = newColor.getBlue();

        int i = (int)(r * 0.7f) + b - 320;
        int j = Math.min(i, 0) / 10;
        int k = Mth.clamp(g + j, 0, 255);

        return ((0) << 24) | ((r & 0xFF) << 16) | ((k & 0xFF) << 8)  | ((b & 0xFF));
    }

    public static int shiftLayer2(int colorInt) {
        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_ITEM_2);

        Color newColor = weightedShift(winged, color, 2, 2, 3, 4);

        int r = Math.min(newColor.getRed() + 16, 255);
        int b = Math.min(newColor.getBlue() + 16, 255);

        return ((0) << 24) | ((r & 0xFF) << 16) | ((newColor.getGreen() & 0xFF) << 8)  | ((b & 0xFF));
    }
}
