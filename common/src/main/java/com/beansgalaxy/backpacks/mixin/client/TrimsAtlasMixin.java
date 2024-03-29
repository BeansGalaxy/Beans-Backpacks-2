package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(PalettedPermutations.class)
public abstract class TrimsAtlasMixin {
    @Mutable @Shadow @Final private List<ResourceLocation> textures;
    @Unique private static final ResourceLocation BACKPACK_TRIMS = new ResourceLocation("beansbackpacks/trims");

    @Redirect(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/atlas/sources/PalettedPermutations;textures:Ljava/util/List;"))
    public List<ResourceLocation> injectBackpackTrims(PalettedPermutations instance, ResourceManager resourceManager) {
        if (textures.stream().anyMatch(in -> in.equals(BACKPACK_TRIMS))) {
            Set<ResourceLocation> resourceLocations =
                    resourceManager.listResources("textures/trims/backpacks", in ->
                            in.getPath().endsWith(".png"))
                    .keySet();

            NonNullList<ResourceLocation> newLocations = NonNullList.create();
            resourceLocations.forEach(in ->
                        newLocations.add(in.withPath(path ->
                                    path.replace("textures/", "").replace(".png", ""))));

            newLocations.addAll(textures);
            newLocations.remove(BACKPACK_TRIMS);
            return newLocations.stream().toList();
        }
        return textures;
    }
}
