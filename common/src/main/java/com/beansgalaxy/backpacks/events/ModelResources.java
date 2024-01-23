package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.HashSet;
import java.util.Map;

public class ModelResources {
      public static HashSet<ModelResourceLocation> get() {
            Map<ResourceLocation, Resource> resourceMap = Minecraft.getInstance().getResourceManager().listResources("models/item/backpack", (p_251575_) -> {
                  String s = p_251575_.getPath();
                  String namespace = p_251575_.getNamespace();
                  return s.endsWith(".json") && namespace.equals(Constants.MOD_ID);
            });

            HashSet<ModelResourceLocation> modelIDs = new HashSet<>();
            for(ResourceLocation resourceLocation: resourceMap.keySet()) {
                  String key = resourceLocation.getPath().replaceAll("models/item/backpack/", "").replaceAll(".json", "");
                  ModelResourceLocation id = new ModelResourceLocation(Constants.MOD_ID, "backpack/" + key, "inventory");
                  modelIDs.add(id);
            }

            return modelIDs;
      }
}
