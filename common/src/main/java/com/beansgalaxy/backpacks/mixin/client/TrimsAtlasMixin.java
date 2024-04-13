package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(PalettedPermutations.class)
public abstract class TrimsAtlasMixin {
    @Mutable @Shadow private List<ResourceLocation> textures;
    @Shadow private Map<String, ResourceLocation> permutations;
    @Unique private static final ResourceLocation BACKPACK_TRIMS = new ResourceLocation("beansbackpacks/trims");

    @Inject(method = "run", at = @At("HEAD"))
    public void injectBackpackTrims(ResourceManager manager, SpriteSource.Output output, CallbackInfo ci) {
        if (textures.stream().anyMatch(in -> in.equals(BACKPACK_TRIMS))) {
            Set<ResourceLocation> resourceLocations =
                        manager.listResources("textures/trims/backpacks", in ->
                                                in.getPath().endsWith(".png"))
                                    .keySet();

            NonNullList<ResourceLocation> newLocations = NonNullList.create();
            resourceLocations.forEach(in ->
                        newLocations.add(in.withPath(path ->
                                    path.replace("textures/", "").replace(".png", ""))));

            newLocations.addAll(textures);
            newLocations.remove(BACKPACK_TRIMS);
            textures = newLocations.stream().toList();

            HashMap<String, ResourceLocation> map = new HashMap<>(permutations);
            if (!Services.COMPAT.isModLoaded("pigsteel"))
                map.remove("pigsteel");
            if (!Services.COMPAT.isModLoaded("enderitemod"))
                map.remove("enderite");

            permutations = map;
        }
        }
}
