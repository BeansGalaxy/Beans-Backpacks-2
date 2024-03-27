package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.features.ElytraFeature;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackWingsModel;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
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

            Traits.LocalData traits = backpack.getTraits();
            if (traits.isEmpty() || traits.kind == null)
                  return;

            Kind kind = traits.kind;
            Color tint = kind.getShiftedColor(traits.color);
            if (Kind.WINGED.is(kind)) {
                  if (backpack.getDirection().getAxis().isHorizontal()) {
                        pose.pushPose();
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                        pose.scale(1.09f, 1.09f, 1.09f);
                        this.wings.renderToBuffer(pose, source.getBuffer(this.model.renderType(ElytraFeature.WINGED_LOCATION)), light,
                                    OverlayTexture.NO_OVERLAY, tint.getRed() / 255f, tint.getGreen() / 255f, tint.getBlue() / 255f, 1f);
                        pose.popPose();
                  }
            }

            if (backpack instanceof EntityAbstract entityAbstract) {
                  double breakTime = entityAbstract.wobble;
                  yaw += (float) (breakTime * Math.sin(breakTime / Math.PI * 4));
                  renderNameAndHitbox(pose, source, entity, yaw, light);
            }

            BackpackInventory.Viewable viewable = backpack.getViewable();
            pose.pushPose();
            viewable.updateOpen();
            this.model.setupPlaced(viewable.headPitch);
            pose.mulPose(Axis.YN.rotationDegrees(yaw));

            float[] colors = {tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F};
            ResourceLocation texture = kind.getAppendedResource(traits.key, "");
            VertexConsumer outer = source.getBuffer(RenderType.entityCutout(texture));
            this.model.renderToBuffer(pose, outer, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);
            pose.popPose();

            RegistryAccess registryAccess = backpack.getCommandSenderWorld().registryAccess();
            double distance = Math.sqrt(this.entityRenderDispatcher.distanceToSqr(entity));
            renderOverlays(pose, light, source, colors, yaw, registryAccess, traits, this.model, this.trimAtlas, distance);

      }

      private static float[] inflate(float scale) {
            return new float[]{(scale + 8)/8f, (scale + 9)/9f, (scale + 4)/4f, -scale/32f};
      }

      public static void renderOverlays(PoseStack pose, int light, MultiBufferSource source, float[] colors, float yaw, RegistryAccess registryAccess, Traits.LocalData traits, BackpackModel model, TextureAtlas atlas, double distance) {
            Screen currentScreen = Minecraft.getInstance().screen;
            float[] deflate = inflate(-0.02f);

            pose.pushPose();
            pose.mulPose(Axis.YN.rotationDegrees(yaw));
            pose.scale(deflate[0], deflate[1], deflate[2]);
            pose.translate(0, deflate[3] * 2, -deflate[3]);
            Kind kind = traits.kind;
            ResourceLocation texture = kind.getAppendedResource(traits.key, "");
            VertexConsumer inner = source.getBuffer(RenderType.entitySmoothCutout(texture));
            model.renderMask(pose, inner, light, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1F);
            pose.popPose();

            CompoundTag trim = traits.getTrim();
            String button = traits.button();
            int color = traits.color;
            if (!button.equals("none") && (!trim.contains("material") || !trim.contains("pattern"))) {
                  if (RenderHelper.isYellow(new Color(color))) {
                        button = "amethyst";
                  }
                  if (!Constants.isEmpty(button)) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("pattern", Constants.MOD_ID + ":trim_button_default");
                        tag.putString("material", button);
                        trim = tag;
                  }
            }

            boolean inBackpackScreen = currentScreen instanceof BackpackScreen || currentScreen instanceof SmithingScreen;
            float[] inflate = inflate(inBackpackScreen ? 0.01f : ((float) distance + 5) * 0.001f);
            TrimHelper.getBackpackTrim(registryAccess, trim).ifPresent((trim1) -> {
                  pose.pushPose();
                  pose.mulPose(Axis.YN.rotationDegrees(yaw));
                  VertexConsumer vc = atlas.getSprite(trim1.backpackTexture(traits.material())).wrap(source.getBuffer(RenderType.entityCutout(Sheets.ARMOR_TRIMS_SHEET)));
                  pose.scale(inflate[0], inflate[1], inflate[2]);
                  pose.translate(0, inflate[3], 0);
                  if (inBackpackScreen) {
                        pose.translate(0, inflate[3], 0);
                        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                        pose.translate(0, -inflate[3] * 4, inflate[3]);
                        pose.scale(1/inflate[0], 1, inflate[2]);
                  }
                  model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                  pose.popPose();
            });

            switch (kind) {
                  case WINGED ->
                        renderInteriorMask(pose, light, source, yaw, model, deflate, kind);
                  case LEATHER -> {
                        renderInteriorMask(pose, light, source, yaw, model, deflate, kind);

                        pose.pushPose();
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                        pose.scale(inflate[0], inflate[1], inflate[2]);
                        pose.translate(0, inflate[3], 0);
                        ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, "textures/entity/leather/leather_overlay.png");
                        VertexConsumer overlayTexture = source.getBuffer(RenderType.entityTranslucentCull(overlay));
                        Color weighted = DyableBackpack.weightedShift(new Color(0xffd7bf), new Color(color), 2.5f, 2.5f, 2.5f, 0);
                        model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, weighted.getRed() / 255f, weighted.getGreen() / 255f, weighted.getBlue() / 255f, 1);
                        pose.popPose();

                        pose.pushPose();
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                        float[] pouchScale = inflate(0.5f);
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
            }
      }

      private static void renderInteriorMask(PoseStack pose, int light, MultiBufferSource source, float yaw, BackpackModel model, float[] deflate, Kind kind) {
            pose.pushPose();
            pose.mulPose(Axis.YN.rotationDegrees(yaw));
            pose.scale(deflate[0], deflate[1], deflate[2]);
            pose.translate(0, deflate[3] * 2, -deflate[3]);
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
                  AABB box = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
                  VertexConsumer vertices = mbs.getBuffer(RenderType.lines());
                  float brightness = Math.min(light, 300) / 300f / 2;
                  float value = 0.2f * brightness;
                  float alpha = 0.9f;
                  if (!entity.getDirection().getAxis().isHorizontal()) {
                        double h = 9D / 16;
                        double w = 8D / 32;
                        double d = 4D / 32;
                        box = new AABB(w, 0, d, -w, h, -d);
                        box.move(-entity.getX(), -entity.getY(), -entity.getZ());
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                        LevelRenderer.renderLineBox(pose, vertices, box, value, value, value, alpha);
                  } else {
                        LevelRenderer.renderLineBox(pose, vertices, box, value, value, value, alpha);
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                  }
                  pose.popPose();
            }
      }

      @Override
      public ResourceLocation getTextureLocation(T entity) {
            return TEXTURE;
      }
}