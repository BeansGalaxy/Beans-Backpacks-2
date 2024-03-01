package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.items.WingedBackpack;
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
        int color = traits.color;

        float scale = backFeature.sneakInter / 3f;
        ItemStack backStack = backData.getStack();
        if (backStack.getItem() instanceof WingedBackpack) {
            color = WingedBackpack.shiftColor(color);
            if (!player.isFallFlying())
                backpackBody.xRot = 0.5f + ((scale - 1) / 5);
            pose.translate(0, ((2 - scale) - (player.isFallFlying() ? 1 : 0)) / 16, (scale / 32));
        } else
            pose.translate(0, (1 / 16f) * scale, (1 / 32f) * scale);

        Color tint = new Color(color);
        ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + traits.key + ".png");
        VertexConsumer vc = mbs.getBuffer(backpackModel.renderType(texture));
        backpackModel.mask.xScale = 0.999f;
        backpackModel.mask.zScale = 0.93f;
        backpackModel.mask.z = 0.4f;
        backpackModel.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);

        RegistryAccess registryAccess = player.getCommandSenderWorld().registryAccess();
        RendererHelper.renderOverlays(pose, light, mbs, tint, registryAccess, traits, backData.getTrim(), backpackModel, this.trimAtlas);
        pose.popPose();
    }
}
