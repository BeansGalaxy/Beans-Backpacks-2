package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;

public class ElytraFeature<T extends LivingEntity, M extends EntityModel<T>> {

    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ElytraModel<T> elytraModel;
    private final BackFeature<T, M> backFeature;

    public ElytraFeature(EntityModelSet loader, BackFeature<T, M> backFeature) {
        this.elytraModel = new ElytraModel<>(loader.bakeLayer(ModelLayers.ELYTRA));
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float animationProgress, float yHeadRot, float headPitch, M parent, BackData backData) {
        ItemStack backStack = backData.getStack();
        ResourceLocation texture = getElytraResource(backStack, backData);

        Traits.LocalData traits = backData.getLocalData();
        int colorInt = backStack.getItem() instanceof WingedBackpack ? WingedBackpack.shiftColor(traits.color) : 0xFFFFFF;
        Color tint = new Color(colorInt);

        pose.pushPose();
        pose.translate(0.0F, 0.0F, 0.125F);
        parent.copyPropertiesTo(this.elytraModel);
        this.elytraModel.setupAnim(entity, limbAngle, limbDistance, animationProgress, yHeadRot, headPitch);
        VertexConsumer vc1 = ItemRenderer.getArmorFoilBuffer(mbs, RenderType.armorCutoutNoCull(texture), false, backStack.hasFoil());
        this.elytraModel.renderToBuffer(pose, vc1, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1.0F);
        pose.popPose();
    }

    private ResourceLocation getElytraResource(ItemStack backStack, BackData backData) {
        if (backStack.is(Services.REGISTRY.getWinged()))
            return new ResourceLocation(Constants.MOD_ID, "textures/entity/elytra/" + backData.backpackInventory.getLocalData().key + ".png");


        if (backStack.is(Items.ELYTRA)) {
            ResourceLocation texture = WINGS_LOCATION;
            AbstractClientPlayer player = (AbstractClientPlayer) backData.owner;
            if (player.isElytraLoaded() && player.getElytraTextureLocation() != null) {
                texture = player.getElytraTextureLocation();
            } else if (player.isCapeLoaded() && player.getCloakTextureLocation() != null && player.isModelPartShown(PlayerModelPart.CAPE)) {
                texture = player.getCloakTextureLocation();
            }
            return texture;
        }

        return new ResourceLocation(Constants.MOD_ID, "textures/entity/elytra/" + backStack.getItem().toString().replace(":", "$") + ".png");
    }


}
