package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
            if (entity instanceof BackpackEntity backpack) {
                  double breakTime = backpack.wobble;
                  return (float) (breakTime * Math.sin(breakTime / Math.PI * 4));
            }
            return 0;
      }

      public void render(T entity, float yaw, float tickDelta, PoseStack pose, MultiBufferSource mbs, int light) {
            super.render(entity, yaw += renderWobble(entity, yaw), tickDelta, pose, mbs, light);
            Backpack bEntity = ((Backpack) entity);
            Traits.LocalData traits = bEntity.getLocalData();

            if (traits.key.isEmpty())
                  return;

            BackpackInventory.Viewable viewable = bEntity.getViewable();
            Kind kind = traits.kind();

            if (kind == null)
                  return;

            pose.pushPose();
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

            if (!bEntity.isMirror())
                  renderHitbox(pose, mbs.getBuffer(RenderType.lines()), entity, yaw, light);
            pose.translate(0, -3 / 16f, 0);
            int colorInt = Kind.WINGED.is(kind) ? WingedBackpack.shiftColor(traits.color) : traits.color;
            Color tint = new Color(colorInt);
            String key = traits.key;
            ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/entity/" + key + ".png");
            VertexConsumer vc = mbs.getBuffer(this.model.renderType(texture));
            this.model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, tint.getRed() / 255F, tint.getGreen() / 255F, tint.getBlue() / 255F, 1F);
            RegistryAccess registryAccess = bEntity.getCommandSenderWorld().registryAccess();
            renderOverlays(pose, light, mbs, tint, registryAccess, traits, this.model, this.trimAtlas);
            pose.popPose();
      }

      private static void renderHitbox(PoseStack pose, VertexConsumer vertices, Entity entity, float yaw, int light) {
            Minecraft minecraft = Minecraft.getInstance();
            HitResult crosshairTarget = minecraft.hitResult;
            if (crosshairTarget.getType() == HitResult.Type.ENTITY && ((EntityHitResult) crosshairTarget).getEntity() == entity && !minecraft.options.hideGui) {
                  AABB box = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
                  float brightness = Math.min(light, 300) / 300f / 2;
                  float value = 0.2f * brightness;
                  float alpha = 0.8f;
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
            } else pose.mulPose(Axis.YN.rotationDegrees(yaw));
      }

      @Override
      public ResourceLocation getTextureLocation(T entity) {
            return TEXTURE;
      }
}
