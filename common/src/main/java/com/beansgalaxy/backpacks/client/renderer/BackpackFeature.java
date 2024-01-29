package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
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
import org.jetbrains.annotations.NotNull;

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
        this.model = new BackpackModel<>(loader.bakeLayer(Constants.BACKPACK_MODEL));
        this.trimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    @Override
    public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource mbs, int light, @NotNull T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float yHeadRot, float headPitch) {
        BackpackModel<?> backpackModel = ((BackpackModel<?>) this.model);
        ModelPart backpackBody = backpackModel.body;

        if (entity instanceof AbstractClientPlayer player) {
            BackData backData = BackData.get(player);
            ItemStack backpackStack = backData.getStack();
            Traits.LocalData traits = backData.backpackInventory.getLocalData();

            if (!Kind.isBackpack(backpackStack))
                return;

            pose.pushPose();
            ModelPart torso = ((PlayerModel<?>) this.getParentModel()).body;
            weld(backpackBody, torso);
            BackpackInventory.Viewable viewable = backData.backpackInventory.getViewable();
            viewable.updateOpen();
            backpackModel.head.xRot = viewable.headPitch;
            sneakInter = sneakInter(player, pose, sneakInter);

            this.model.setupAnim(entity, limbAngle, limbDistance, tickDelta, animationProgress, yHeadRot);
            Color tint = new Color(traits.color);
            ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + traits.key + ".png");
            VertexConsumer vc = mbs.getBuffer(this.model.renderType(texture));
            backpackModel.mask.xScale = 0.999f;
            backpackModel.mask.zScale = 0.93f;
            backpackModel.mask.z = 0.4f;
            this.model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);

            RegistryAccess registryAccess = entity.getCommandSenderWorld().registryAccess();
            RendererHelper.renderOverlays(pose, light, mbs, tint, registryAccess, traits, (BackpackModel) this.model, this.trimAtlas);
            pose.popPose();
        }
    }
}
