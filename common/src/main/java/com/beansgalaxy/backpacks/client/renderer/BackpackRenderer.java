package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
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
import org.joml.Matrix4f;

import java.awt.*;

import static com.beansgalaxy.backpacks.client.RendererHelper.renderOverlays;

public class BackpackRenderer<T extends Entity> extends EntityRenderer<T> {
      ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack/null.png");
      private final BackpackModel model;
      private final TextureAtlas trimAtlas;

      public BackpackRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
            this.model = new BackpackModel(ctx.bakeLayer(RendererHelper.BACKPACK_MODEL));
            this.trimAtlas = ctx.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
      }

      private float renderWobble(Entity entity, float yaw) {
            if (entity instanceof EntityAbstract backpack) {
                  double breakTime = backpack.wobble;
                  return (float) (breakTime * Math.sin(breakTime / Math.PI * 4));
            }
            return 0;
      }

      public void render(T entity, float yaw, float tickDelta, PoseStack pose, MultiBufferSource mbs, int light) {
            Backpack bEntity = ((Backpack) entity);
            Traits.LocalData traits = bEntity.getLocalData();

            if (traits.key.isEmpty())
                  return;

            BackpackInventory.Viewable viewable = bEntity.getViewable();
            Kind kind = traits.kind();

            if (kind == null)
                  return;

            yaw += renderWobble(entity, yaw);

            if (!bEntity.isMirror()) {
                  renderHitbox(pose, mbs, entity, yaw, light);
            }

            pose.pushPose();
            pose.mulPose(Axis.YN.rotationDegrees(yaw));
            viewable.updateOpen();
            model.head.xRot = viewable.headPitch;
            model.body.z = -4;
            model.body.yRot = (float) (180 * (Math.PI / 180));
            model.body.xRot = (float) (180 * (Math.PI / 180));

            ModelPart mask = this.model.mask;
            mask.xScale = 0.99f;
            mask.yScale = 1f;
            mask.zScale = 0.96f;
            mask.z = 0.2f;

            pose.translate(0, -3 / 16f, 0);
            int colorInt = Kind.WINGED.is(kind) ? WingedBackpack.shiftColor(traits.color) : traits.color;
            Color tint = new Color(colorInt);
            String key = traits.key;
            ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + key + ".png");
            VertexConsumer vc = mbs.getBuffer(this.model.renderType(texture));
            this.model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);
            RegistryAccess registryAccess = bEntity.getCommandSenderWorld().registryAccess();

            CompoundTag trim = bEntity.getTrim();

            renderOverlays(pose, light, mbs, tint, registryAccess, traits, trim, this.model, this.trimAtlas);
            pose.popPose();
      }

      private void renderHitbox(PoseStack pose, MultiBufferSource mbs, T entity, float yaw, int light) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() == entity && !minecraft.options.hideGui) {
                  if (this.shouldShowName(entity)) {
                        Component displayName = entity.getDisplayName();
                        if (!displayName.getContents().toString().equals("empty")) {
                              pose.pushPose();
                              renderNameTag(entity, displayName, pose, mbs, light);
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
      protected void renderNameTag(T entity, Component $$1, PoseStack pose, MultiBufferSource mbs, int light) {
            double $$5 = this.entityRenderDispatcher.distanceToSqr(entity);
            if (!($$5 > 4096.0)) {
                  boolean $$6 = !entity.isDiscrete();
                  Direction direction = entity.getDirection();
                  float y = entity.getNameTagOffsetY();
                  double yOff = entity.getEyeY() - entityRenderDispatcher.camera.getPosition().y;
                  y -= yOff / 16;
                  int $$8 = "deadmau5".equals($$1.getString()) ? -10 : 0;
                  pose.translate(direction.getStepX() * 5/16f, y, direction.getStepZ() * 5/16f);
                  pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
                  pose.scale(-0.02F, -0.02F, 1F);
                  Matrix4f $$9 = pose.last().pose();
                  float $$10 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                  int $$11 = (int)($$10 * 255.0F) << 24;
                  Font $$12 = this.getFont();
                  float $$13 = (float)(-$$12.width($$1) / 2);
                  $$12.drawInBatch($$1, $$13, (float)$$8, 553648127, false, $$9, mbs, $$6 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, $$11, light);
                  if ($$6) {
                        $$12.drawInBatch($$1, $$13, (float)$$8, -1, false, $$9, mbs, Font.DisplayMode.NORMAL, 0, light);
                  }
            }
      }

      @Override
      public ResourceLocation getTextureLocation(T entity) {
            return TEXTURE;
      }
}
