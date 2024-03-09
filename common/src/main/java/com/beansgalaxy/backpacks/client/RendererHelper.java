package com.beansgalaxy.backpacks.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorMaterials;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Map;

public interface RendererHelper {
    ModelLayerLocation BACKPACK_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_model"), "main");
    ModelLayerLocation PACK_WINGS_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "backpack_wings_model"), "main");
      ModelLayerLocation POT_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "pot_player"), "main");
      ModelLayerLocation CAULDRON_MODEL = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "cauldron_player"), "main");
    Map<String, ResourceLocation> ButtonIdentifiers = ImmutableMap.of(
                "gold", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/gold.png"),
                "amethyst", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/amethyst.png"),
                "diamond", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/diamond.png"),
                "netherite", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/netherite.png"));

      static void renderOverlays(PoseStack pose, int light, MultiBufferSource mbs, Color tint, RegistryAccess registryAccess, Traits.LocalData data, CompoundTag trim, BackpackModel model, TextureAtlas atlas) {
        Kind kind = data.kind();
        if (kind.isTrimmable() && trim.get("material") != null && trim.get("pattern") != null)
            TrimHelper.getBackpackTrim(registryAccess, trim).ifPresent((trim1) ->
                        renderTrim(model, pose, light, mbs, atlas.getSprite(trim1.backpackTexture(getMaterial(data.key)))));
        else renderButton(kind, tint, model, pose, light, mbs, data.key);
    }

    static void weld(ModelPart welded, ModelPart weldTo) {
        welded.xRot = weldTo.xRot;
        welded.yRot = weldTo.yRot;
        welded.zRot = weldTo.zRot;
        welded.x = weldTo.x;
        welded.y = weldTo.y;
        welded.z = weldTo.z;
    }

    static float sneakInter(Entity entity, float sneakInter) {
        if (entity.isCrouching())
            sneakInter += sneakInter < 3 ? 1 : 0;
        else {
            sneakInter -= sneakInter > 1 ? 1 : 0;
            sneakInter -= sneakInter > 0 ? 1 : 0;
        }
        return sneakInter;
    }

    static void renderTrim(EntityModel<Entity> model, PoseStack pose, int light, MultiBufferSource mbs, TextureAtlasSprite sprite) {
        VertexConsumer vc = sprite.wrap(mbs.getBuffer(Sheets.armorTrimsSheet()));
        if (inBackpackScreen()) {
            pose.scale(1.001f, 0.995f, 1);
            for (int i = 0; i < 8; i++) {
                pose.scale(1.001f, 1.001f, 1.001f);
                model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
            }
        } else model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
    }

    static void renderButton(Kind b$kind, Color tint, EntityModel<Entity> model, PoseStack pose, int light, MultiBufferSource mbs, String key) {
          ResourceLocation identifier = null;
          switch (b$kind) {
                case METAL -> identifier = ButtonIdentifiers.get("diamond");
                case UPGRADED -> identifier = ButtonIdentifiers.get("netherite");
                case LEATHER, WINGED ->
                {
                      VertexConsumer interior = mbs.getBuffer(RenderType.entityTranslucent(new ResourceLocation(Constants.MOD_ID, "textures/entity/" + key +"_interior.png")));
                      model.renderToBuffer(pose, interior, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

                      if (isYellow(tint))
                            identifier = ButtonIdentifiers.get("amethyst");
                      else
                            identifier = ButtonIdentifiers.get("gold");
                }
          }

          if (identifier != null) {
                VertexConsumer buttonVc = mbs.getBuffer(RenderType.entityCutout(identifier));
                model.renderToBuffer(pose, buttonVc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
          }

          if (b$kind == Kind.LEATHER) {
                ResourceLocation overlayIdentifier = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + key + "_overlay.png");
                VertexConsumer overlayTexture = mbs.getBuffer(RenderType.entityTranslucent(overlayIdentifier));
                model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.5f);
          }
    }

    static ArmorMaterials getMaterial(String key) {
        for (ArmorMaterials armorMaterial: ArmorMaterials.values()) {
            String armor = armorMaterial.name().toLowerCase();

            if (armor.equals(key))
                return armorMaterial;
        }
        return ArmorMaterials.LEATHER;
    }

    private static boolean inBackpackScreen() {
        Screen currentScreen = Minecraft.getInstance().screen;
        return currentScreen instanceof BackpackScreen || currentScreen instanceof SmithingScreen;
    }

    private static boolean isYellow(Color tint) {
        int red = tint.getRed();
        int blue = tint.getBlue();
        int green = tint.getGreen();

        // BRIGHTNESS
        if (red + green + blue > 600) return false;
        //DARKNESS
        if (red + green <333) return false;

        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        if (min == max) return false;

        float hue;

        if (max == red)
            hue = (green - blue) / (max - min);
        else if (max == green)
            hue = 2f + (blue - red) / (max - min);
        else
            hue = 4f + (red - green) / (max - min);

        hue = hue * 60;
        if (hue < 0) hue = hue + 360;

        // LOWER TOWARDS RED, HIGHER TOWARDS GREEN
        return 40 < Math.round(hue) && 60 > Math.round(hue);
    }

    static void renderBackpackForSmithing(GuiGraphics graphics, float x, float y, Entity renderedEntity) {
          graphics.pose().pushPose();

          int scale = 50;
          graphics.pose().translate(x, y - 8, 50.0);
          Quaternionf rotation = new Quaternionf().rotationXYZ(0.43633232F, 3.5f, (float) Math.PI);

          graphics.pose().mulPoseMatrix(new Matrix4f().scaling((float) scale, (float) scale, (float)(-scale)));
          Lighting.setupForEntityInInventory();
          graphics.pose().mulPose(rotation);
          EntityRenderDispatcher $$8 = Minecraft.getInstance().getEntityRenderDispatcher();

          $$8.setRenderShadow(false);
          RenderSystem.runAsFancy(() -> $$8.render(renderedEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880));
          graphics.flush();
          $$8.setRenderShadow(true);
          graphics.pose().popPose();
          Lighting.setupFor3DItems();
    }
}
