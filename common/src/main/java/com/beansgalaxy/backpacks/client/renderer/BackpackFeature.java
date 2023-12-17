package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.TrimHelper;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmorStandModel;
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
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.RendererHelper.*;

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
    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float yHeadRot, float headPitch) {
        BackpackModel<?> backpackModel = ((BackpackModel<?>) this.model);
        ModelPart backpackBody = backpackModel.body;

        if (entity instanceof AbstractClientPlayer player) {
            BackSlot backSlot = BackSlot.get(player);
            ItemStack backpackStack = backSlot.getItem();
            BackpackInventory.Data data = backSlot.getData();

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

    private void rotateArmorStand(ArmorStand armorStand, boolean isBackpack, PoseStack pose, ArmorStandModel standModel) {
        armorStand.setLeftLegPose(new Rotations(-1, 0, isBackpack ? 180 : -1));
        armorStand.setRightLegPose(new Rotations(1, 0, isBackpack ? 180 : 1));
        armorStand.setLeftArmPose(new Rotations(-10, 0, isBackpack ? 90 : -10));
        armorStand.setRightArmPose(new Rotations(-15, 0, isBackpack ? -90 : 10));
        armorStand.setHeadPose(new Rotations(0, 0, isBackpack ? 180 : 0));
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

    private void renderOverlays(PoseStack pose, int light, MultiBufferSource mbs, Entity entity, ItemStack backpackStack, Kind b$kind, Color tint, String key) {
        CompoundTag nbt = backpackStack.getTag();
        CompoundTag trim = nbt != null ? backpackStack.getTag().getCompound("Trim") : null;
        if (b$kind.isTrimmable() && trim != null)
            TrimHelper.getBackpackTrim(entity.getCommandSenderWorld().registryAccess(), trim).ifPresent((trim1) ->
                    renderTrim(this.model, pose, light, mbs, this.trimAtlas.getSprite(trim1.backpackTexture(getMaterial(key)))));
        else renderButton(b$kind, tint, model, pose, light, mbs);
    }
}
