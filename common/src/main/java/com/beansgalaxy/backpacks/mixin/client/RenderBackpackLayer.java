package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.client.renderer.BackpackFeature;
import com.beansgalaxy.backpacks.client.renderer.PotFeature;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class RenderBackpackLayer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
      public RenderBackpackLayer(EntityRendererProvider.Context $$0, PlayerModel<AbstractClientPlayer> $$1, float $$2) {
            super($$0, $$1, $$2);
      }

      @Inject(method = "<init>", at = @At("TAIL"))
      public void appendBackpackLayer(EntityRendererProvider.Context ctx, boolean $$1, CallbackInfo ci) {
            this.addLayer(new BackpackFeature<>(this, ctx.getModelSet(), ctx.getModelManager()));
            this.addLayer(new PotFeature<>(this, ctx.getModelSet()));
      }
}
