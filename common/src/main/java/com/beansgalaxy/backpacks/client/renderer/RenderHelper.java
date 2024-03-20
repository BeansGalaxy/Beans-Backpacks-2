package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;

public interface RenderHelper {
      ModelLayerLocation BACKPACK_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_model"), "main");
      ModelLayerLocation PACK_WINGS_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_wings_model"), "main");
      ModelLayerLocation POT_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "pot_player"), "main");
      ModelLayerLocation CAULDRON_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "cauldron_player"), "main");

      static boolean isYellow(Color tint) {
            int red = tint.getRed();
            int blue = tint.getBlue();
            int green = tint.getGreen();

            // BRIGHTNESS
            if (red + green + blue > 600) return false;
            //DARKNESS
            if (red + green < 333) return false;

            float min = Math.min(Math.min(red, green), blue);
            float max = Math.max(Math.max(red, green), blue);

            if (min == max) return false;

            float hue;

            if (max == red)
                  hue = (green - blue) / (max - min);
            else if (max == green)
                  hue = 2f + (blue - red) / (max - min);
            else
                  hue = 4f + (red - green) / (max - min);

            hue = hue * 60;
            if (hue < 0) hue = hue + 360;

            // LOWER TOWARDS RED, HIGHER TOWARDS GREEN
            return 40 < Math.round(hue) && 60 > Math.round(hue);
      }

      ClampedItemPropertyFunction IS_YELLOW_ITEM_PREDICATE = (itemStack, clientLevel, livingEntity, i) -> {
            CompoundTag display = itemStack.getTagElement("display");
            if (display == null || !display.contains("color"))
                  return 0;

            int color = display.getInt("color");
            if (!RenderHelper.isYellow(new Color(color)))
                  return 0;

            return 1;
      };
}
