package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Traits;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.RendererHelper.sneakInter;
import static com.beansgalaxy.backpacks.client.RendererHelper.weld;

public class BackpackFeature<T extends LivingEntity, M extends EntityModel<T>> {

    private final BackpackModel<Player> backpackModel;
    private final TextureAtlas trimAtlas;
    private final BackFeature<T, M> backFeature;

    public BackpackFeature(EntityModelSet loader, ModelManager modelManager, BackFeature<T, M> backFeature) {
        this.backpackModel = new BackpackModel<>(loader.bakeLayer(RendererHelper.BACKPACK_MODEL));
        this.trimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, BackData backData) {
        ModelPart backpackBody = backpackModel.body;
        Traits.LocalData traits = backData.backpackInventory.getLocalData();

        pose.pushPose();
        weld(backpackBody, torso);
        BackpackInventory.Viewable viewable = backData.backpackInventory.getViewable();
        viewable.updateOpen();
        backpackModel.head.xRot = viewable.headPitch;
        backFeature.sneakInter = sneakInter(player, pose, backFeature.sneakInter);

        Color tint = new Color(traits.color);
        ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + traits.key + ".png");
        VertexConsumer vc = mbs.getBuffer(backpackModel.renderType(texture));
        backpackModel.mask.xScale = 0.999f;
        backpackModel.mask.zScale = 0.93f;
        backpackModel.mask.z = 0.4f;
        backpackModel.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);

        RegistryAccess registryAccess = player.getCommandSenderWorld().registryAccess();
        RendererHelper.renderOverlays(pose, light, mbs, tint, registryAccess, traits, backpackModel, this.trimAtlas);
        pose.popPose();
    }
}
