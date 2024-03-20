package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.renderer.features.ElytraFeature;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackWingsModel;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
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
            Color tint;
            if (Kind.WINGED.is(kind)) {
                  tint = new Color(WingedBackpack.shiftColor(traits.color));
                  if (backpack.getDirection().getAxis().isHorizontal()) {
                        pose.pushPose();
                        pose.mulPose(Axis.YN.rotationDegrees(yaw));
                        pose.scale(1.09f, 1.09f, 1.09f);
                        this.wings.renderToBuffer(pose, source.getBuffer(this.model.renderType(ElytraFeature.WINGED_LOCATION)), light,
                                    OverlayTexture.NO_OVERLAY, tint.getRed() / 255f, tint.getGreen() / 255f, tint.getBlue() / 255f, 1f);
                        pose.popPose();
                  }
            }
            else tint = new Color(traits.color);

            if (backpack instanceof EntityAbstract entityAbstract) {
                  double breakTime = entityAbstract.wobble;
                  yaw += (float) (breakTime * Math.sin(breakTime / Math.PI * 4));
                  renderNameAndHitbox(pose, source, entity, yaw, light);
            }

            BackpackInventory.Viewable viewable = backpack.getViewable();
            pose.pushPose();
            pose.mulPose(Axis.YN.rotationDegrees(yaw));
            viewable.updateOpen();
            this.model.setupPlaced(viewable.headPitch);

            ResourceLocation texture = kind.getAppendedResource(traits.key, "");
            VertexConsumer vc = source.getBuffer(this.model.renderType(texture));
            this.model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);
            RegistryAccess registryAccess = backpack.getCommandSenderWorld().registryAccess();
            renderOverlays(pose, light, source, tint, registryAccess, traits, this.model, this.trimAtlas);

            ResourceLocation interiorResource = kind.getAppendedResource(traits.key, Kind.is(kind, Kind.LEATHER, Kind.WINGED) ? "_interior" : "");
            VertexConsumer interior = source.getBuffer(RenderType.entityTranslucent(interiorResource));
            model.renderMask(pose, interior, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

            pose.popPose();
      }

      public static void renderOverlays(PoseStack pose, int light, MultiBufferSource mbs, Color tint, RegistryAccess registryAccess, Traits.LocalData traits, BackpackModel model, TextureAtlas atlas) {
            CompoundTag trim = traits.getTrim();
            String button = traits.button();
            if (!button.equals("none") || !trim.contains("material") || !trim.contains("pattern")) {
                  if (RenderHelper.isYellow(new Color(traits.color))) {
                        button = "amethyst";
                  }
                  if (!Constants.isEmpty(button)) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("pattern", Constants.MOD_ID + ":trim_button_default");
                        tag.putString("material", button);
                        trim = tag;
                  }
            }

            TrimHelper.getBackpackTrim(registryAccess, trim).ifPresent((trim1) -> {
                  VertexConsumer vc = atlas.getSprite(trim1.backpackTexture(traits.material())).wrap(mbs.getBuffer(Sheets.armorTrimsSheet()));
                  Screen currentScreen = Minecraft.getInstance().screen;
                  if (currentScreen instanceof BackpackScreen || currentScreen instanceof SmithingScreen) {
                        pose.scale(1.001f, 0.995f, 1);
                        for (int i = 0; i < 8; i++) {
                              pose.scale(1.001f, 1.001f, 1.001f);
                              model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                        }
                  } else
                        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

            });

            Kind kind = traits.kind;
            if (Kind.LEATHER.is(kind)) {
                  ResourceLocation overlay = kind.getAppendedResource("", "_overlay");
                  VertexConsumer overlayTexture = mbs.getBuffer(RenderType.entityTranslucent(overlay));
                  model.renderToBuffer(pose, overlayTexture, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.5f);
            }
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