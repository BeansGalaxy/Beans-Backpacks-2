package com.beansgalaxy.backpacks.events;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class LoadItemModels implements ModelLoadingPlugin {
      @Override
      public void onInitializeModelLoader(Context ctx) {
            for(ModelResourceLocation modelId : ModelResources.get())
                  ctx.addModels(modelId);
      }
}
