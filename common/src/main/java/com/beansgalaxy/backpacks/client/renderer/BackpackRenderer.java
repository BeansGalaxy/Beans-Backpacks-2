package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.Tint;
import com.beansgalaxy.backpacks.client.renderer.features.ElytraFeature;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackWingsModel;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;

public class BackpackRenderer<T extends Entity> extends EntityRenderer<T> {
      ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack/null.png");
      private final BackpackModel<T> model;
      private final BackpackWingsModel<T> wings;
      private final TextureAtlas trimAtlas;

      public BackpackRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
            this.model = new BackpackModel<>(ctx.bakeLayer(RenderHelper.BACKPACK_MODEL));
            this.trimAtlas = ctx.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
            this.wings = new BackpackWingsModel<>(ctx.bakeLayer(RenderHelper.PACK_WINGS_MODEL));
      }

      @Override
      public void render(@NotNull T entity, float yaw, float tick, @NotNull PoseStack pose, @NotNull MultiBufferSource source, int light) {
            Backpack backpack = (Backpack) entity;
            if (backpack.isRemoved())
                  return;

            Traits.LocalData traits = backpack.getTraits();
            if (traits.isEmpty() || traits.kind == null)
                  return;


            Viewable viewable = backpack.getViewable();
            EntityAbstract abstractBackpack = null;
            if (backpack instanceof EntityAbstract entityAbstract) {
                  abstractBackpack = entityAbstract;
                  double breakTime = entityAbstract.wobble;
                  yaw += (float) ((breakTime * 0.80f) * Math.sin(breakTime / Math.PI * 3));
                  renderNameAndHitbox(pose, source, entity, yaw, light);

                  if (viewable.lastDelta > tick)
                        viewable.updateOpen();

                  float headPitch = Mth.lerp(tick, viewable.lastPitch, viewable.headPitch) * 0.37f;
                  this.model.setupPlaced(headPitch);
                  viewable.lastDelta = tick;
            }
            else {
                  float headPitch = Mth.lerp(viewable.lastDelta, viewable.lastPitch, viewable.headPitch) * 0.25f;
                  this.model.setupPlaced(headPitch);
            }

            pose.pushPose();
            pose.mulPose(Axis.YN.rotationDegrees(yaw));

            Kind kind = traits.kind;
            Color tint = kind.getShiftedColor(traits.color);
            if (Kind.WINGED.is(kind)) {
                  if (backpack.getDirection().getAxis().isHorizontal()) {
                        pose.pushPose();
                        pose.scale(1.09f, 1.09f, 1.09f);
                        this.wings.renderToBuffer(pose, source.getBuffer(this.model.renderType(ElytraFeature.WINGED_LOCATION)), light,
                                    OverlayTexture.NO_OVERLAY, tint.getRed() / 255f, tint.getGreen() / 255f, tint.getBlue() / 255f, 1f);
                        pose.popPose();
                  }
            }

            pose.pushPose();
            float[] colors = {tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F};
            ResourceLocation texture = kind.getAppendedResource(traits.backpack_id, "");
            VertexConsumer outer = source.getBuffer(RenderType.entityCutout(texture));
            this.model.renderToBuffer(pose, outer, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1);
            pose.popPose();

            RegistryAccess registryAccess = backpack.getCommandSenderWorld().registryAccess();
            double distance = Math.sqrt(this.entityRenderDispatcher.distanceToSqr(entity));

            Screen currentScreen = Minecraft.getInstance().screen;
            boolean inBackpackScreen = currentScreen instanceof BackpackScreen || currentScreen instanceof SmithingScreen;
            float inflate = inBackpackScreen ? 0.02f : ((float) distance + 5) * 0.002f;
            float deflate = distance < 10 || inBackpackScreen ? 0 : (float) (distance - 10.0) * -0.03f;
            renderOverlays(pose, light, source, colors, registryAccess, traits, this.model, this.trimAtlas, inflate, deflate, false);

            if (abstractBackpack != null) {
                  int breakAmount = abstractBackpack.breakAmount;
                  if (breakAmount > 0) {
                        pose.pushPose();
                        int breakStage = Math.min(Mth.ceil(breakAmount / 3f), 7);
                        ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "textures/entity/destroy_stage/" + breakStage + ".png");
                        VertexConsumer crumble = source.getBuffer(RenderType.crumbling(location));
                        this.model.renderToBuffer(pose, crumble, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
                        pose.popPose();
                  }
            }
            pose.popPose();
      }

      private static float[] inflate(float scale, boolean reverseY) {
            return new float[]{
                        (scale + 8)/8f,
                        (scale + 9)/9f,
                        (scale + 4)/4f,
                        (reverseY ? 1 : -1) * scale/32f,
                        (reverseY ? -5 : -1) * scale/32};
      }

      public static void renderOverlays(PoseStack pose, int light, MultiBufferSource source, float[] colors, RegistryAccess registryAccess, Traits.LocalData traits, BackpackModel model, TextureAtlas atlas, float inflateScale, float deflateScale, boolean reverseY) {
            float[] inflate = inflate(inflateScale, reverseY);
            float[] deflate = inflate(deflateScale, reverseY);

            pose.pushPose();
            pose.scale(deflate[1], inflate[1], deflate[2] / 2 + 0.5f);
            pose.translate(0, inflate[3] * 2, -deflate[3] / 3);
            Kind kind = traits.kind;
            ResourceLocation texture = kind.getAppendedResource(traits.backpack_id, "");
            VertexConsumer inner = source.getBuffer(RenderType.entitySmoothCutout(texture));
            model.renderMask(pose, inner, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);
            pose.popPose();

            CompoundTag trim = traits.getTrim();
            String button = traits.button();
            int color = traits.color;
            if (!button.equals("none") && (!trim.contains("material") || !trim.contains("pattern"))) {
                  if (Tint.isYellow(color)) {
                        button = "amethyst";
                  }
                  else if (Kind.BIG_BUNDLE.is(kind) &&Tint.isGreen(color)) {//
                        button = "copper";
                  }
                  if (!Constants.isEmpty(button)) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("pattern", Constants.MOD_ID + ":trim_button_default");
                        tag.putString("material", button);
                        trim = tag;
                  }
            }

            boolean noClipTrims = false;
            switch (kind) {
                  case WINGED ->
                        renderInteriorMask(pose, light, source, model, inflate, deflate, kind);
                  case LEATHER -> {
                        renderInteriorMask(pose, light, source, model, inflate, deflate, kind);
                        Color weighted = DyableBackpack.weightedShift(new Color(0xffd7bf), new Color(color), 2.5f, 2.5f, 2.5f, 0);
                        float[] floats = {weighted.getRed() / 255f, weighted.getGreen() / 255f, weighted.getBlue() / 255f};
                        renderDyableColorOverlay(pose, light, source, model, inflate, floats, "leather/leather");

                        pose.pushPose();
                        float[] pouchScale = inflate(0.5f, reverseY);
                        pose.scale(pouchScale[0], pouchScale[1], pouchScale[2]);
                        pose.translate(0, pouchScale[3]/2, 0);
                        ResourceLocation pouch = new ResourceLocation(Constants.MOD_ID, "textures/entity/leather/pouch.png");
                        VertexConsumer pouchTexture = source.getBuffer(RenderType.entityCutoutNoCull(pouch));
                        model.renderToBuffer(pose, pouchTexture, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1);

                        pose.scale(inflate[0], inflate[1], inflate[2]);
                        pose.translate(0, inflate[3], 0);
                        ResourceLocation pouchOverlay = new ResourceLocation(Constants.MOD_ID, "textures/entity/leather/pouch_overlay.png");
                        VertexConsumer pouchOverlayTexture = source.getBuffer(RenderType.entityTranslucentCull(pouchOverlay));
                        model.renderToBuffer(pose, pouchOverlayTexture, light, OverlayTexture.NO_OVERLAY, weighted.getRed() / 255f, weighted.getGreen() / 255f, weighted.getBlue() / 255f, 0.8f);
                        pose.popPose();
                  }
                  case BIG_BUNDLE -> {
                        int rgb = DyableBackpack.shiftBundleColor(color).getRGB();
                        Tint tint = new Tint(rgb);
                        Tint.HSL hsv = tint.HSL().rotate(230);
                        double brightness = tint.brightness();
                        double sat = hsv.getSat();
                        hsv.setLum(Math.cbrt(brightness)).scaleSat(Math.sqrt(sat));
                        hsv.push();
                        renderDyableColorOverlay(pose, light, source, model, inflate, tint.getFloats(), "back_bundle/bundle");
                        renderInteriorMask(pose, light, source, model, inflate, deflate, kind);

                        noClipTrims = true;
                        pose.pushPose();
                        pose.scale(inflate[0], inflate[1], inflate[2] * 2 - 1);
                        pose.translate(0, inflate[3], inflate[4]);
                        ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, "textures/entity/back_bundle/bundle_highlight.png");
                        VertexConsumer overlayTexture = source.getBuffer(RenderType.entityTranslucentCull(overlay));
                        Tint highlight = new Tint(color);
                        Tint.HSL hsl = highlight.HSL();
                        double lum = hsl.getLum();
                        hsl.setLum((Math.cbrt(lum + 0.2) + lum) / 2).rotate(5).setSat(Math.sqrt((hsl.getSat() + brightness) / 2));
                        hsl.push();
                        float[] f = highlight.getFloats();
                        model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, f[0], f[1], f[2], 0.6f);
                  }
            }

            TrimHelper.getBackpackTrim(registryAccess, trim).ifPresent((trim1) -> {
                  pose.pushPose();
                  pose.scale(inflate[0], inflate[1], inflate[2] * 2 - 1);
                  pose.pushPose();
                  VertexConsumer vc = atlas.getSprite(trim1.backpackTexture(traits.material())).wrap(source.getBuffer(RenderType.entityCutout(Sheets.ARMOR_TRIMS_SHEET)));
                  pose.translate(0, -inflate[3], inflate[4]);
                  model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                  pose.popPose();

                  pose.translate(0, inflate[3] * 3, 0);
                  pose.scale(1, 1, 1);
                  model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                  pose.popPose();
            });

            if (noClipTrims) pose.popPose();
      }

      private static void renderDyableColorOverlay(PoseStack pose, int light, MultiBufferSource source, BackpackModel<?> model, float[] inflate, float[] color, String path) {
            pose.pushPose();
            pose.scale(inflate[0], inflate[1], inflate[2] * 2 - 1);
            pose.translate(0, inflate[3], inflate[4]);
            ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + path + "_overlay.png");
            VertexConsumer overlayTexture = source.getBuffer(RenderType.entityTranslucentCull(overlay));
            model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1);
            pose.popPose();
      }

      private static void renderInteriorMask(PoseStack pose, int light, MultiBufferSource source, BackpackModel<?> model, float[] inflate, float[] deflate, Kind kind) {
            pose.pushPose();
            pose.scale(deflate[1], inflate[1], deflate[2] / 2 + 0.5f);
            pose.translate(0, inflate[3], -deflate[3] / 3);
            ResourceLocation pouchTexture = kind.getAppendedResource("", "_interior");
            VertexConsumer pouch = source.getBuffer(RenderType.entityCutoutNoCull(pouchTexture));
            model.renderMask(pose, pouch, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1f);
            pose.popPose();
      }

      private void renderNameAndHitbox(PoseStack pose, MultiBufferSource mbs, T entity, float yaw, int light) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() == entity && !minecraft.options.hideGui) {
                  if (this.shouldShowName(entity)) {
                        Component displayName = entity.getDisplayName();
                        if (!Constants.isEmpty(displayName)) {
                              pose.pushPose();
                              double $$5 = this.entityRenderDispatcher.distanceToSqr(entity);
                              if (!($$5 > 4096.0)) {
                                    Direction direction = entity.getDirection();
                                    float y = entity.getNameTagOffsetY();
                                    double yOff = entity.getEyeY() - entityRenderDispatcher.camera.getPosition().y;
                                    y -= (float) (yOff / 16);
                                    pose.translate(direction.getStepX() * 5/16f, y, direction.getStepZ() * 5/16f);
                                    pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
                                    pose.scale(-0.02F, -0.02F, 1F);
                                    Matrix4f $$9 = pose.last().pose();
                                    float $$10 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                                    int $$11 = (int)($$10 * 255.0F) << 24;
                                    net.minecraft.client.gui.Font $$12 = this.getFont();
                                    float $$13 = (float)(-$$12.width(displayName) / 2);
                                    $$12.drawInBatch(displayName, $$13, 0, 553648127, false, $$9, mbs, Font.DisplayMode.SEE_THROUGH, $$11, light);
                                    $$12.drawInBatch(displayName, $$13, 0, -1, false, $$9, mbs, Font.DisplayMode.NORMAL, 0, light);
                              }
                              pose.popPose();
                        }
                  }

                  pose.pushPose();
                  VertexConsumer vertices = mbs.getBuffer(RenderType.lines());
                  float brightness = Math.min(light, 300) / 300f / 2;
                  float value = 0.3f * brightness;
                  float alpha = 1f;
                  AABB box;
                  if (!entity.getDirection().getAxis().isHorizontal()) {
                        double h = 9D / 16;
                        double w = 8D / 32;
                        double d = 4D / 32;
                        box = new AABB(w, 0, d, -w, h, -d);
                        box.move(-entity.getX(), -entity.getY(), -entity.getZ());
                  } else {
                        box = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
                        float yRot = entity.getDirection().toYRot();
                        yaw += yRot;
                  }

                  pose.mulPose(Axis.YN.rotationDegrees(yaw));
                  LevelRenderer.renderLineBox(pose, vertices, box, value, value, value, alpha);
                  pose.popPose();
            }
      }

      @Override
      public ResourceLocation getTextureLocation(T entity) {
            return TEXTURE;
      }
}