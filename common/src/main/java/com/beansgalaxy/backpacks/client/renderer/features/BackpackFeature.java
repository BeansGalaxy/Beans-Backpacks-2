package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
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
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.renderer.features.BackFeature.weld;

public class BackpackFeature<T extends LivingEntity, M extends EntityModel<T>> {

    private final BackpackModel<Player> backpackModel;
    private final TextureAtlas trimAtlas;
    private final BackFeature<T, M> backFeature;

    public BackpackFeature(EntityModelSet loader, ModelManager modelManager, BackFeature<T, M> backFeature) {
        this.backpackModel = new BackpackModel<>(loader.bakeLayer(RenderHelper.BACKPACK_MODEL));
        this.trimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, BackData backData) {
        ModelPart backpackBody = backpackModel.body;
        ModelPart backpackMask = backpackModel.mask;
        Traits.LocalData traits = backData.getTraits();

        pose.pushPose();
        weld(backpackBody, torso);
        weld(backpackMask, torso);
        BackpackInventory.Viewable viewable = backData.backpackInventory.getViewable();
        viewable.updateOpen();
        backpackModel.body.getChild("head").xRot = viewable.headPitch;
        backpackModel.mask.getChild("head").xRot = viewable.headPitch;
        int color = traits.color;
        Color tint = new Color(color);
        float scale = backFeature.sneakInter / 3f;
        ItemStack backStack = backData.getStack();
        if (backStack.getItem() instanceof WingedBackpack) {
            tint = WingedBackpack.shiftColor(color);
            if (!player.isFallFlying()) {
                backpackBody.xRot = 0.5f + ((scale - 1) / 5);
                backpackMask.xRot = 0.5f + ((scale - 1) / 5);
                }
            pose.translate(0, ((2 - scale) - (player.isFallFlying() ? 1 : 0)) / 16, (scale / 32));
        } else
            pose.translate(0, (1 / 16f) * scale, (1 / 32f) * scale);

        float[] colors = {tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F};
        ResourceLocation texture = traits.kind.getAppendedResource(traits.key, "");
        VertexConsumer vc = mbs.getBuffer(backpackModel.renderType(texture));
        backpackModel.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);

        RegistryAccess registryAccess = player.getCommandSenderWorld().registryAccess();
        BackpackRenderer.renderOverlays(pose, light, mbs, colors, backpackBody.yRot, registryAccess, traits, backpackModel, this.trimAtlas, 32);
        pose.popPose();
    }
}
