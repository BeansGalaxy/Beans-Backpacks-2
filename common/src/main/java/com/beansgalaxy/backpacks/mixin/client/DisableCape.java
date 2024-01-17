package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.core.BackData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class DisableCape {
      @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), cancellable = true)
      public void disable(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer player, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, CallbackInfo ci) {
            BackData backData = BackData.get(player);
            if (!backData.isEmpty())
                  ci.cancel();
      }
}
