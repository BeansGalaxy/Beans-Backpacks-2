package com.beansgalaxy.backpacks.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorMaterials;

import java.awt.*;
import java.util.Map;

public interface RendererHelper {
    ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack/null.png");
    ResourceLocation OVERLAY_LEATHER = new ResourceLocation(Constants.MOD_ID, "textures/entity/leather_overlay.png");
    Map<String, ResourceLocation> ButtonIdentifiers = ImmutableMap.of(
                "gold", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/gold.png"),
                "amethyst", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/amethyst.png"),
                "diamond", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/diamond.png"),
                "netherite", new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/netherite.png"));


    static void renderOverlays(PoseStack pose, int light, MultiBufferSource mbs, Color tint, RegistryAccess registryAccess, BackpackInventory.Data data, BackpackModel model, TextureAtlas atlas) {
        Kind kind = data.kind;
        CompoundTag trim = data.trim;
        if (kind.isTrimmable() && trim.get("material") != null && trim.get("pattern") != null)
            TrimHelper.getBackpackTrim(registryAccess, trim).ifPresent((trim1) ->
                        renderTrim(model, pose, light, mbs, atlas.getSprite(trim1.backpackTexture(getMaterial(data.key)))));
        else renderButton(kind, tint, model, pose, light, mbs);
    }


    static void weld(ModelPart welded, ModelPart weldTo) {
        welded.xRot = weldTo.xRot;
        welded.yRot = weldTo.yRot;
        welded.zRot = weldTo.zRot;
        welded.x = weldTo.x;
        welded.y = weldTo.y;
        welded.z = weldTo.z;
    }

    static float sneakInter(Entity entity, PoseStack pose, float sneakInter) {
        float scale = sneakInter / 3f;
        pose.translate(0, (1 / 16f) * scale, (1 / 32f) * scale);
        if (entity.isCrouching())
            sneakInter += sneakInter < 3 ? 1 : 0;
        else {
            sneakInter -= sneakInter > 1 ? 1 : 0;
            sneakInter -= sneakInter > 0 ? 1 : 0;
        }
        return sneakInter;
    }

    static void renderTrim(EntityModel<Entity> model, PoseStack pose, int light, MultiBufferSource mbs, TextureAtlasSprite sprite) {
        VertexConsumer vc = sprite.wrap(mbs.getBuffer(Sheets.armorTrimsSheet(false)));
        if (inBackpackScreen()) {
            for (int j = 1; j < 4; j++) {
                VertexConsumer vc1 = sprite.wrap(mbs.getBuffer(Sheets.armorTrimsSheet(false)));
                float scale = (0.0015F * j);
                pose.scale(1 + scale, 1 + scale / 2, 1 + scale);
                pose.translate(0, -scale / 1.1, 0);
                model.renderToBuffer(pose, vc1, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
            }
        } else
            model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
    }

    static void renderButton(Kind b$kind, Color tint, EntityModel<Entity> model, PoseStack pose, int light, MultiBufferSource mbs) {
        if (b$kind == Kind.LEATHER) {
            VertexConsumer overlayTexture = mbs.getBuffer(RenderType.entityTranslucent(OVERLAY_LEATHER));
            model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
        ResourceLocation identifier = null;
        switch (b$kind) {
            case METAL -> identifier = ButtonIdentifiers.get("diamond");
            case UPGRADED -> identifier = ButtonIdentifiers.get("netherite");
            case LEATHER -> {
                if (isYellow(tint)) identifier = ButtonIdentifiers.get("amethyst");
                else identifier = ButtonIdentifiers.get("gold");
            }
        }
        if (identifier != null) {
            VertexConsumer buttonVc = mbs.getBuffer(RenderType.entityCutout(identifier));
            model.renderToBuffer(pose, buttonVc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
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
        return false;
        //return currentScreen instanceof BackpackScreen || currentScreen instanceof SmithingScreen;
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

}
