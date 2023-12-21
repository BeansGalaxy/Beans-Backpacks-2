package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

import static com.beansgalaxy.backpacks.client.RendererHelper.sneakInter;

public class PotFeature<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/clay_detail.png");
    private final EntityModel<Entity> model;
    private float sneakInter = 0;

    public PotFeature(RenderLayerParent<T, M> context, EntityModelSet loader) {
        super(context);
        this.model = new PotModel<>(loader.bakeLayer(Constants.POT_MODEL));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float yHeadRot, float headPitch) {
        ItemStack backpackStack;
        if (entity instanceof AbstractClientPlayer player)
            backpackStack = player.inventoryMenu.slots.get(BackSlot.SLOT_INDEX).getItem();
        else
            backpackStack = ItemStack.EMPTY;
        if (!backpackStack.is(Items.DECORATED_POT))
            return;


        CompoundTag nbt = new CompoundTag();
        if (backpackStack.getTag() != null)
            nbt = backpackStack.getTag().getCompound("BlockEntityTag");

        DecoratedPotBlockEntity.Decorations sherds = DecoratedPotBlockEntity.Decorations.load(nbt);

        pose.pushPose();

        PotModel<?> potModel = (PotModel<?>) this.model;
        ModelPart torso = ((PlayerModel<?>) this.getParentModel()).body;
        for (int j = 0; j < potModel.getModelParts().size(); j++) {
            ModelPart modelPart = potModel.getModelParts().get(j);
            RendererHelper.weld(modelPart, torso);
        }
        sneakInter = sneakInter(entity, pose, sneakInter);

        this.model.setupAnim(entity, limbAngle, limbDistance, tickDelta, animationProgress, yHeadRot);
        VertexConsumer vc = mbs.getBuffer(this.model.renderType(TEXTURE));
        potModel.renderBody(pose, vc, light, OverlayTexture.NO_OVERLAY, true);
        potModel.renderDetail(pose, mbs, light, OverlayTexture.NO_OVERLAY, sherds);

        pose.popPose();
    }

    // COULDN'T ATTACH BACKPACK DIRECTLY TO TORSO THUS THIS MATCHES ITS MOVEMENTS WHEN CROUCHING
    public void sneaking(Entity entity, PoseStack pose) {
        float scale = sneakInter / 3f;
        pose.translate(0, (1 / 16f) * scale, (1 / 32f) * scale);
        if (entity.isCrouching())
            sneakInter += sneakInter < 3 ? 1 : 0;
        else {
            sneakInter -= sneakInter > 1 ? 1 : 0;
            sneakInter -= sneakInter > 0 ? 1 : 0;
        }
    }
}
