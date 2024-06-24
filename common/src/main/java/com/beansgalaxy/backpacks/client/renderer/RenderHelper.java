package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.Tint;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public interface RenderHelper {
      ModelLayerLocation BACKPACK_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_model"), "main");
      ModelLayerLocation PACK_WINGS_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_wings_model"), "main");
      ModelLayerLocation PACK_CAPE_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_cape_model"), "main");
      ModelLayerLocation POT_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "pot_player"), "main");
      ModelLayerLocation CAULDRON_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "cauldron_player"), "main");

      ClampedItemPropertyFunction IS_YELLOW_ITEM_PREDICATE = (itemStack, clientLevel, livingEntity, i) -> {
            CompoundTag display = itemStack.getTagElement("display");
            if (display == null || !display.contains("color"))
                  return 0;

            int color = display.getInt("color");
            return Tint.isYellow(color) ? 1f
                 : Tint.isGreen(color) ? 0.5f
                 : 0;
      };
}
