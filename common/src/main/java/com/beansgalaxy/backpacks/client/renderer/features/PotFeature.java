package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.PotModel;
import com.beansgalaxy.backpacks.core.BackData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

import static com.beansgalaxy.backpacks.client.RendererHelper.sneakInter;

public class PotFeature<T extends LivingEntity, M extends EntityModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/clay_detail.png");
    private final PotModel<Player> potModel;
    private final BackFeature<T, M> backFeature;

    public PotFeature(EntityModelSet loader, BackFeature<T, M> backFeature) {
        this.potModel = new PotModel<>(loader.bakeLayer(RendererHelper.POT_MODEL));
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, ItemStack backStack) {
        CompoundTag nbt = new CompoundTag();
        if (backStack.getTag() != null)
            nbt = backStack.getTag().getCompound("BlockEntityTag");

        DecoratedPotBlockEntity.Decorations sherds = DecoratedPotBlockEntity.Decorations.load(nbt);
        pose.pushPose();
        for (int j = 0; j < potModel.getModelParts().size(); j++) {
            ModelPart modelPart = potModel.getModelParts().get(j);
            RendererHelper.weld(modelPart, torso);
        }

        float scale = backFeature.sneakInter / 3f;
        pose.translate(0, (1 / 16f) * scale, (1 / 32f) * scale);
        VertexConsumer vc = mbs.getBuffer(potModel.renderType(TEXTURE));
        potModel.renderBody(pose, vc, light, OverlayTexture.NO_OVERLAY);
        potModel.renderDetail(pose, mbs, light, OverlayTexture.NO_OVERLAY, sherds);

        pose.popPose();
    }

}
