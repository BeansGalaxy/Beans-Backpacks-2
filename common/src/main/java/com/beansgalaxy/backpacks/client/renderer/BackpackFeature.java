package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.RendererHelper.sneakInter;
import static com.beansgalaxy.backpacks.client.RendererHelper.weld;

public class BackpackFeature<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private final EntityModel<Entity> model;
    private final TextureAtlas trimAtlas;
    private float sneakInter = 0;

    public BackpackFeature(RenderLayerParent<T, M> context, EntityModelSet loader, ModelManager modelManager) {
        super(context);
        this.model = new BackpackModel<>(loader.bakeLayer(RendererHelper.BACKPACK_MODEL));
        this.trimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float yHeadRot, float headPitch) {
        BackpackModel<?> backpackModel = ((BackpackModel<?>) this.model);
        ModelPart backpackBody = backpackModel.body;

        if (entity instanceof AbstractClientPlayer player) {
            BackSlot backSlot = BackSlot.get(player);
            ItemStack backpackStack = backSlot.getItem();
            BackpackInventory.Data data = backSlot.backpackInventory.getData();

            if (!Kind.isBackpack(backpackStack))
                return;

            pose.pushPose();
            ModelPart torso = ((PlayerModel<?>) this.getParentModel()).body;
            weld(backpackBody, torso);
            backSlot.viewable.updateOpen();
            backpackModel.head.xRot = backSlot.viewable.headPitch;
            sneakInter = sneakInter(player, pose, sneakInter);

            this.model.setupAnim(entity, limbAngle, limbDistance, tickDelta, animationProgress, yHeadRot);
            Color tint = new Color(data.color);
            ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + data.key + ".png");
            VertexConsumer vc = mbs.getBuffer(this.model.renderType(texture));
            backpackModel.mask.xScale = 0.999f;
            backpackModel.mask.zScale = 0.93f;
            backpackModel.mask.z = 0.4f;
            this.model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);

            RegistryAccess registryAccess = entity.getCommandSenderWorld().registryAccess();
            RendererHelper.renderOverlays(pose, light, mbs, tint, registryAccess, data, (BackpackModel) this.model, this.trimAtlas);
            pose.popPose();
        }
    }
}
