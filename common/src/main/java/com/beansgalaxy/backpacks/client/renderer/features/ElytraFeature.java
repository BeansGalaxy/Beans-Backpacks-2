package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
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
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ElytraFeature<T extends LivingEntity, M extends EntityModel<T>> {

    private static final ResourceLocation ELYTRA_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    public static final ResourceLocation WINGED_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/entity/elytra/winged.png");
    private final ElytraModel<T> elytraModel;
    private final BackFeature<T, M> backFeature;

    public ElytraFeature(EntityModelSet loader, BackFeature<T, M> backFeature) {
        this.elytraModel = new ElytraModel<>(loader.bakeLayer(ModelLayers.ELYTRA));
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float animationProgress, float yHeadRot, float headPitch, M parent, BackData backData) {
        ItemStack backStack = backData.getStack();
        ResourceLocation texture = getElytraResource(backStack, backData);

        Traits.LocalData traits = backData.getTraits();
        int colorInt = backStack.getItem() instanceof WingedBackpack ? WingedBackpack.shiftColor(traits.color).getRGB() : 0xFFFFFF;
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
        if (backStack.is(Services.REGISTRY.getWinged())) {
            return WINGED_LOCATION;
        }

        if (backStack.is(Items.ELYTRA)) {
            ResourceLocation texture = ELYTRA_LOCATION;
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

    @NotNull
    public static ResourceLocation getWingedBackpackResource(String key) {
        return new ResourceLocation(Constants.MOD_ID, "textures/entity/elytra/winged.png");
    }


}
