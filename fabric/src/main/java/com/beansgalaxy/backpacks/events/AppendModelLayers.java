package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.client.renderer.BackpackFeature;
import com.beansgalaxy.backpacks.client.renderer.PotFeature;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class AppendModelLayers implements LivingEntityFeatureRendererRegistrationCallback {
    @Override
    public void registerRenderers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> entityRenderer, RegistrationHelper registrationHelper, EntityRendererProvider.Context ctx) {
        if (entityRenderer instanceof PlayerRenderer) {
            registrationHelper.register(new BackpackFeature<>(entityRenderer, ctx.getModelSet(), ctx.getModelManager()));
            registrationHelper.register(new PotFeature<>(entityRenderer, ctx.getModelSet()));
        }
    }
}
