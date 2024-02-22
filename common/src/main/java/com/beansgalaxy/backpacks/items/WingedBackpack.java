package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class WingedBackpack extends DyableBackpack {
    public static final int WINGED_COLOR = 8359839;

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
        return WINGED_COLOR;
    }

    public static int shiftColor(int colorInt) {
        if (colorInt == WINGED_COLOR)
            return WINGED_COLOR;

        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_COLOR);

        Color out = new Color(
                28 + (3 * color.getRed() + winged.getRed()) / 4,
                28 + (3 * color.getGreen() + winged.getGreen()) / 4,
                28 + (2 * color.getBlue() + winged.getBlue()) / 3,
                255);

        return out.getRGB();
    }

    public static int decodeColor(int colorInt) {
        Color color = new Color(colorInt);
        Color winged = new Color(WINGED_COLOR);

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
