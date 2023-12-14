package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class BakeItemModels {

      @Redirect(method = "<init>", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/resources/model/ModelBakery;loadTopLevel(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V"))
      private void init(ModelBakery instance, ModelResourceLocation $$0) {
            this.loadTopLevel(ItemRenderer.SPYGLASS_IN_HAND_MODEL);

            Map<ResourceLocation, Resource> resourceLocationResourceMap = Minecraft.getInstance().getResourceManager().listResources("models/item/backpack", (p_251575_) -> {
                  String s = p_251575_.getPath();
                  String namespace = p_251575_.getNamespace();
                  return s.endsWith(".json") && namespace.equals(Constants.MOD_ID);
            });

            for(ResourceLocation resourceLocation: resourceLocationResourceMap.keySet()) {
                  String key = resourceLocation.getPath().replaceAll("models/item/backpack/", "").replaceAll(".json", "");
                  System.out.println(key);
                  Constants.BACKPACK_KEYS.add(key);
            }

            for(String key : Constants.BACKPACK_KEYS)
                  this.bakeBackpackModel(key);
      }

      @Unique private void bakeBackpackModel(String key) {
            this.loadTopLevel(new ModelResourceLocation(Constants.MOD_ID, "backpack/" + key, "inventory"));
      }

      @Shadow protected abstract void loadTopLevel(ModelResourceLocation resourceLocation);

}
