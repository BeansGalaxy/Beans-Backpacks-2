package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public interface RenderHelper {
      ModelLayerLocation BACKPACK_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_model"), "main");
      ModelLayerLocation PACK_WINGS_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_wings_model"), "main");
      ModelLayerLocation POT_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "pot_player"), "main");
      ModelLayerLocation CAULDRON_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "cauldron_player"), "main");

}
