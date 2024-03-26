package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.models.CauldronModel;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Optional;

public class CauldronFeature<T extends LivingEntity, M extends EntityModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/cauldron.png");
    private final CauldronModel<Player> cauldronModel;
    private final BackFeature<T, M> backFeature;
    private final TextureAtlas blocksAtlas;

    public CauldronFeature(EntityModelSet loader, ModelManager modelManager, BackFeature<T, M> backFeature) {
        this.cauldronModel = new CauldronModel<>(loader.bakeLayer(RenderHelper.CAULDRON_MODEL));
        this.blocksAtlas = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS);
        this.backFeature = backFeature;
    }

    public void render(PoseStack pose, MultiBufferSource mbs, int light, ModelPart torso, ItemStack backStack) {
        pose.pushPose();
        float scale = backFeature.sneakInter / 3f;

        pose.mulPose(new Quaternionf().rotationXYZ(torso.xRot, torso.yRot, torso.zRot));
        pose.translate(0, -17/16f + (0.18 * scale), 5/16f - (0.096f * scale) + 0.001f);
        VertexConsumer cauldVC = mbs.getBuffer(cauldronModel.renderType(TEXTURE));
        cauldronModel.renderToBuffer(pose, cauldVC, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        if (backStack.hasTag() && backStack.getTag().contains("back_slot")) {
            CompoundTag fluidTag = backStack.getTagElement("back_slot");
            if (fluidTag.contains("id") && fluidTag.contains("amount")) {
                int amount = fluidTag.getInt("amount");
                if (amount > 0) {
                    Item bucket = BuiltInRegistries.ITEM.get(new ResourceLocation(fluidTag.getString("id")));
                    pose.scale(0.5f, 0.5f, 0.5f);
                    int cappedAmount = (int) (amount / (CauldronInventory.MAX_SIZE / 6f));
                    pose.translate(0, -(cappedAmount / 8f) + 12 / 8f + (cappedAmount == 0 ? -0.01 : 0), 0);
                    if (bucket instanceof BucketItemAccess access) {
                        Fluid fluid = access.beans_Backpacks_2$getFluid();
                        CauldronInventory.FluidAttributes attributes = Services.COMPAT.getFluidTexture(fluid, blocksAtlas);
                        VertexConsumer fluidVC = attributes.sprite().wrap(mbs.getBuffer(RenderType.translucent()));
                        cauldronModel.fluid.render(pose, fluidVC, light, OverlayTexture.NO_OVERLAY, attributes.tint().getRed() / 255f, attributes.tint().getGreen() / 255f, attributes.tint().getBlue() / 255f, 1);
                    } else if (bucket instanceof BucketsAccess access) {
                        Optional<BlockState> optional = access.getBlockState();
                        optional.ifPresent(blockState -> {
                            VertexConsumer blockVC = mbs.getBuffer(ItemBlockRenderTypes.getRenderType(blockState, false));
                            Minecraft instance = Minecraft.getInstance();
                            TextureAtlasSprite particleIcon = instance.getBlockRenderer().getBlockModel(blockState).getParticleIcon();
                            Color color = new Color(instance.getBlockColors().getColor(blockState, null, null, 0));
                            cauldronModel.fluid.render(pose, particleIcon.wrap(blockVC), light, OverlayTexture.NO_OVERLAY, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
                        });
                    }
                }
            }
        }
        pose.popPose();
    }

}
