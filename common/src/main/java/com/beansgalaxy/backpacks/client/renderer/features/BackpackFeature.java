package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackCapeModel;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.renderer.features.BackFeature.weld;

public class BackpackFeature<T extends LivingEntity, M extends EntityModel<T>> {
    private final BackpackModel<Player> backpackModel;
    private final BackpackCapeModel<Player> capeModel;
    private final TextureAtlas trimAtlas;
    private final BackFeature<T, M> backFeature;

    public BackpackFeature(EntityModelSet loader, ModelManager modelManager, BackFeature<T, M> backFeature) {
        this.backpackModel = new BackpackModel<>(loader.bakeLayer(RenderHelper.BACKPACK_MODEL));
        this.capeModel = new BackpackCapeModel<>(loader.bakeLayer(RenderHelper.PACK_CAPE_MODEL));
        this.trimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, BackData backData, float tick) {
        Traits.LocalData traits = backData.getTraits();

        pose.pushPose();
        pose.mulPose(new Quaternionf().rotationXYZ(torso.xRot, torso.yRot, torso.zRot));
        float scale = backFeature.sneakInter / 3f;
        weld(backpackModel.main, torso);
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);

        pose.translate(0, 12/16f, 0);
        Kind kind = traits.kind;
        if (kind.is(Kind.WINGED) || Kind.isWings(chestStack)) {
            setUpWings(player, scale, pose);
        }
        else {
            boolean hasChestplate = !chestStack.isEmpty();
            pose.translate(0, (0.24 * scale) + (hasChestplate ? 0.02 : 0),
                        -(0.096 * scale) + (hasChestplate ? 0.065 : 0.001));
        }

        Viewable viewable = backData.getBackpackInventory().getViewable();
        if (viewable.lastDelta > tick)
            viewable.updateOpen();

        viewable.lastDelta = tick;
        float fallDistance = player.fallDistance;
        float fallPitch = player.isFallFlying() ? 0 : (float) (Math.log(fallDistance * 3 + 1)) * -0.05f;
        float headPitch = Mth.lerp(tick, viewable.lastPitch, viewable.headPitch) * 0.3f;
        backpackModel.setOpenAngle(headPitch + fallPitch);

        ResourceLocation cloakTexture = player.getCloakTextureLocation();
        if (player.isCapeLoaded() && !player.isInvisible() && player.isModelPartShown(PlayerModelPart.CAPE) && cloakTexture != null) {
            if (chestStack.isEmpty())
                pose.translate(0, 1/16f, 0);
            weld(capeModel.cape, backpackModel.main);
            capeModel.cape.yRot = (float) Math.PI * 2;
            capeModel.cape.xRot = -headPitch;
            capeModel.cape.y = fallPitch * 6 - 11f;
            capeModel.cape.z = 2f;
            //ResourceLocation $$0 = new ResourceLocation(Constants.MOD_ID, "textures/cape_template.png");
            RenderType renderType = RenderType.entitySolid(cloakTexture);
            VertexConsumer vertexConsumer = mbs.getBuffer(renderType);
            capeModel.cape.render(pose, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }

        Color tint = kind.getShiftedColor(traits.color);
        float[] colors = {tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F};

        ResourceLocation texture = kind.getAppendedResource(traits.backpack_id, "");
        VertexConsumer vc = mbs.getBuffer(backpackModel.renderType(texture));
        backpackModel.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);

        double distance = Math.sqrt(Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(player));
        float inflate = ((float) distance + 1) * 0.001f;
        float deflate = (float) (distance) * -0.01f;
        RegistryAccess registryAccess = player.getCommandSenderWorld().registryAccess();
        BackpackRenderer.renderOverlays(pose, light, mbs, colors, registryAccess, traits, backpackModel, this.trimAtlas, inflate, deflate, true);
        pose.popPose();
    }

    private void setUpWings(AbstractClientPlayer player, float scale, PoseStack poseStack) {
        boolean fallFlying = player.isFallFlying();
        float wingSpread;
        if (fallFlying) {
            Vec3 deltaMovement = player.getDeltaMovement();
            Vec3 norm = deltaMovement.normalize();
            if (norm.y > 0)
                wingSpread = 0;
            else wingSpread = (float) Math.pow(-norm.y, 1.5);
        }
        else wingSpread = 1;

        float xRot = 0.3f * wingSpread;
        float z = Mth.lerp(scale, fallFlying ? xRot : 4/16f, -3/32f);
        float y = Mth.lerp(scale, fallFlying ? -1/16f : 0, 3/16f);
        poseStack.translate(0, y, z);
        poseStack.mulPose(new Quaternionf().rotationXYZ(Mth.lerp(scale, xRot, 0), 0, 0));
    }
}
