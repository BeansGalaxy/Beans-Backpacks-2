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

    public static int shiftColor(int colorInt) {
        if (colorInt == WINGED_ENTITY)
            return WINGED_ENTITY;

        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_ENTITY);

        int r = (3 * color.getRed() + winged.getRed()) / 4;
        int g = (3 * color.getGreen() + winged.getGreen()) / 4;
        int b = (2 * color.getBlue() + winged.getBlue()) / 3;

        r += 20;
        g += 20;
        b += 20;

        Color out = new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));

        return out.getRGB();
    }

    public static int shiftColorLayer0(int colorInt) {
        if (colorInt == WINGED_ENTITY)
            return WINGED_ITEM_0;

        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_ITEM_0);

        int r = (color.getRed() * 2 + winged.getRed() * 3) / 5;
        int g = (color.getGreen() * 2 + winged.getGreen() * 3) / 5;
        int b = (color.getBlue() * 2 + winged.getBlue() * 3) / 5;

        r += 10;
        g += 10;
        b += 10;

        int i = (int)(r * 0.7f) + b - 320;
        int j = Math.min(i, 0) / 10;
        int k = Math.max(g + j, 0);

        Color out = new Color(Math.min(r, 255), Math.min(k, 255), Math.min(b, 255));

        return out.getRGB();
    }

    public static int shiftColorLayer2(int colorInt) {
        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_ITEM_2);

        int r = (2 * color.getRed() + winged.getRed()) / 3;
        int g = (2 * color.getGreen() + winged.getGreen()) / 3;
        int b = (2 * color.getBlue() + winged.getBlue()) / 3;

        r = Math.min(r + 20, 255);
        g = Math.min(g + 4, 255);
        b = Math.min(b + 20, 255);

        return new Color(r, g, b).getRGB();
    }

    public static int decodeColor(int colorInt) {
        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_ENTITY);

        float r = color.getRed();
        float g = color.getGreen();
        float b = color.getBlue();

        r -= 28;
        g -= 28;
        b -= 28;

        r *= 4;
        g *= 4;
        b *= 3;

        r -= winged.getRed();
        g -= winged.getGreen();
        b -= winged.getBlue();

        r /= 3;
        g /= 3;
        b /= 2;

        int rgb = 65536 * ((int) r) + 256 * ((int) g) + ((int) b);

        return rgb;
    }
}
