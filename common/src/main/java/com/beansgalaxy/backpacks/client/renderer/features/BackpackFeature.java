package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.Kind;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

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

    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer player, ModelPart torso, BackData backData, float tick) {
        ModelPart backpackBody = backpackModel.body;
        ModelPart backpackMask = backpackModel.mask;
        Traits.LocalData traits = backData.getTraits();

        pose.pushPose();
        weld(backpackBody, torso);
        weld(backpackMask, torso);

        float scale = backFeature.sneakInter / 3f;
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (traits.kind.is(Kind.WINGED) || Kind.isWings(chestStack)) {
            setUpWings(player, scale, backpackBody, backpackMask);
        }
        else if (chestStack.isEmpty()) {
            backpackBody.z += 1/16f;
            backpackMask.z += 3/32f;
        }
        else {
            float y = (1 / 16f) * scale;
            backpackBody.y += y;
            backpackMask.y += y;
            float z = (1 / 32f) * scale;
            backpackBody.z += z;
            backpackMask.z += z;
            if (player.isCrouching()) {
                backpackBody.z += 17 / 16f;
                backpackMask.z += 35 / 32f;
                backpackBody.y -= 10 / 32f;
                backpackMask.y -= 11 / 32f;
            } else {
                backpackBody.z += 17 / 16f;
                backpackMask.z += 35 / 32f;
                backpackBody.y += 3 / 16f;
                backpackMask.y += 3 / 16f;
            }
        }

        Viewable viewable = backData.getBackpackInventory().getViewable();
        if (viewable.lastDelta > tick)
            viewable.updateOpen();

        float fallDistance = player.fallDistance;
        float fallPitch = player.isFallFlying() ? 0 : (float) (Math.log(fallDistance * 3 + 1)) * -0.05f;
        float headPitch = Mth.lerp(tick, viewable.lastPitch, viewable.headPitch) * 0.3f;
        backpackModel.body.getChild("head").xRot = headPitch + fallPitch;
        backpackModel.mask.getChild("head").xRot = headPitch + fallPitch;
        int color = traits.color;
        Color tint;
        if (traits.kind.is(Kind.WINGED))
            tint = WingedBackpack.shiftColor(color);
        else tint = new Color(color);

        float[] colors = {tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F};
        ResourceLocation texture = traits.kind.getAppendedResource(traits.backpack_id, "");
        VertexConsumer vc = mbs.getBuffer(backpackModel.renderType(texture));
        backpackModel.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);

        RegistryAccess registryAccess = player.getCommandSenderWorld().registryAccess();
        BackpackRenderer.renderOverlays(pose, light, mbs, colors, backpackBody.yRot, registryAccess, traits, backpackModel, this.trimAtlas, 32);
        pose.popPose();
    }

    private void setUpWings(AbstractClientPlayer player, float scale, ModelPart backpackBody, ModelPart backpackMask) {
        float y = ((2 - scale) - (player.isFallFlying() ? 1 : 0)) / 16;
        backpackBody.y += y;
        backpackMask.y += y;
        float z = scale / 32;
        backpackBody.z += z;
        backpackMask.z += z;
        float wingSpread;
        if (player.isFallFlying()) {
            Vec3 deltaMovement = player.getDeltaMovement();
            Vec3 norm = deltaMovement.normalize();
            if (norm.y > 0)
                wingSpread = 0;
            else wingSpread = (float) Math.pow(-norm.y, 1.5);
        }
        else wingSpread = 1;
        backpackBody.xRot = (0.5f + (scale - 1) / 5) * wingSpread;
        backpackMask.xRot = (0.5f + (scale - 1) / 5) * wingSpread;
    }
}
