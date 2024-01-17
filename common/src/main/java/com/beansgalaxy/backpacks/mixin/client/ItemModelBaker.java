package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Map;

@Mixin(value = ModelBakery.class)
public abstract class ItemModelBaker {
      @Shadow protected abstract void loadTopLevel(ModelResourceLocation resourceLocation);

      // IF THERE IS A PROBLEM WRONG WITH FORGE INIT, THE STACK TRACE COMMONLY POINT HERE INSTEAD.
      // BE AWARE THAT THIS CLASS *MIGHT* BE THE PROBLEM, BUT LIKELY ISN'T.
      @Redirect(method = "<init>", at = @At(value = "INVOKE", ordinal = 3,
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
      private void init(ProfilerFiller instance, String string) {
            instance.popPush(string);

            Map<ResourceLocation, Resource> resourceLocationResourceMap = Minecraft.getInstance().getResourceManager().listResources("models/item/backpack", (p_251575_) -> {
                  String s = p_251575_.getPath();
                  String namespace = p_251575_.getNamespace();
                  return s.endsWith(".json") && namespace.equals(Constants.MOD_ID);
            });
            HashSet<String> keys = new HashSet<>();
            for(ResourceLocation resourceLocation: resourceLocationResourceMap.keySet()) {
                  String key = resourceLocation.getPath().replaceAll("models/item/backpack/", "").replaceAll(".json", "");
                  keys.add(key);
            }

            for(String key : keys)
                  this.loadTopLevel(new ModelResourceLocation(Constants.MOD_ID, "backpack/" + key, "inventory"));
      }

}
