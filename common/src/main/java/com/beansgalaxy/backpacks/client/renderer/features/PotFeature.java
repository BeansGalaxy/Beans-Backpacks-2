package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.models.PotModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.joml.Quaternionf;

public class PotFeature<T extends LivingEntity, M extends EntityModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/clay_detail.png");
    private final PotModel<Player> potModel;
    private final BackFeature<T, M> backFeature;

    public PotFeature(EntityModelSet loader, BackFeature<T, M> backFeature) {
        this.potModel = new PotModel<>(loader.bakeLayer(RenderHelper.POT_MODEL));
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, ItemStack backStack) {
        CompoundTag nbt = new CompoundTag();
        if (backStack.getTag() != null)
            nbt = backStack.getTag().getCompound("BlockEntityTag");

        DecoratedPotBlockEntity.Decorations sherds = DecoratedPotBlockEntity.Decorations.load(nbt);
        pose.pushPose();

        float scale = backFeature.sneakInter / 3f;
        boolean hasChestplate = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        pose.mulPose(new Quaternionf().rotationXYZ(torso.xRot, torso.yRot, torso.zRot));
        pose.translate(0,
                    -.02f + (0.18 * scale) + (hasChestplate ? 0.02 : 0),
                    -1/16f - (0.096 * scale) + (hasChestplate ? 0.065 : 0.001));

        VertexConsumer vc = mbs.getBuffer(potModel.renderType(TEXTURE));
        potModel.renderBody(pose, vc, light, OverlayTexture.NO_OVERLAY);
        potModel.renderDetail(pose, mbs, light, OverlayTexture.NO_OVERLAY, sherds);

        pose.popPose();
    }

}
