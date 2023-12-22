package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector3f;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
      private static final Component TITLE = Component.literal("");
      private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/backpack.png");
      private final BackpackMenu handler;

      public BackpackScreen(BackpackMenu menu, Inventory inv, Component title) {
            super(menu, inv, TITLE);
            this.handler = menu;
            this.imageHeight = 256;
      }

      protected void containerTick() {
            super.containerTick();
            if (handler.viewer instanceof LocalPlayer viewer) {
                  boolean hasMoved = !handler.owner.position().closerThan(handler.ownerPos.getCenter(), 2d);
                  boolean notInRange = !handler.owner.position().closerThan(viewer.position(), 5.0d);
                  boolean yawChanged = false;
                  if (handler.owner instanceof RemotePlayer owner) {
                        yawChanged = !BackpackInventory.yawMatches(handler.ownerYaw, owner.yBodyRotO, 35);
                  }
                  if (hasMoved || notInRange || yawChanged)
                        viewer.clientSideCloseContainer();
            }
      }

      @Override
      protected void init() {
            super.init();
            titleLabelY = 1000;
            inventoryLabelY = imageHeight - 216 + handler.invOffset;
      }

      @Override
      public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
            renderBackground(ctx, mouseX, mouseY, delta);
            super.render(ctx, mouseX, mouseY, delta);
            renderTooltip(ctx, mouseX, mouseY);
      }

      @Override
      protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            int j = this.handler.invOffset + topPos;
            context.blit(TEXTURE, leftPos, j - 123, 0, 0, 0, imageWidth, imageHeight, 256, 256);
            drawBackpack(context, width / 2, j, 205, this.handler.mirror, mouseX, mouseY);
      }

      private void drawBackpack(GuiGraphics context, int x, int y, int scale, Backpack entity, int mouseX, int mouseY) {
            context.pose().pushPose();
            context.enableScissor(x - 80, y - 220, x + 80, y + 36);
            float relX = -((width / 2f) - mouseX);
            float relY = (height / 2f) - mouseY - (height / 2f);
            float h = (float) (Math.atan(relX) * Math.atan(Math.pow(relX, 4) / (width * width * 1500))) * 2;
            float g = Math.max(Math.abs(h), Math.abs(relX / 150));
            int i = relX > 0 ? 1 : -1;
            context.pose().translate(x, y + 40 - mouseY / 14f, 50);
            context.pose().mulPose(Axis.XP.rotationDegrees(relY / 14 - 10));
            context.pose().mulPose(Axis.YP.rotation(i * g / 2));
            context.pose().scale(scale, -scale, scale);
            EntityRenderDispatcher entRD = Minecraft.getInstance().getEntityRenderDispatcher();
            Lighting.setupFor3DItems();
            RenderSystem.setShaderLights(new Vector3f(0f, 10f, 0.4f), new Vector3f(0f, -10f, 0.4f));
            RenderSystem.runAsFancy(() ->
                        entRD.render(entity, 0D, 0D, 0D, 20, 1F, context.pose(), context.bufferSource(), 0xFF00FF));
            context.flush();
            context.pose().popPose();
            context.disableScissor();
            Lighting.setupFor3DItems();
      }

      @Override
      protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
            boolean inventory = // BOUNDS OF THE INVENTORY
                        mouseX < (double)left
                                    || mouseY < (double)(top + 100)
                                    || mouseX >= (double)(left + this.imageWidth)
                                    || mouseY >= (double)(top + this.imageHeight);
//            boolean backpackSlots = // BOUNDS OF BACKPACK SLOTS
//                        mouseX < (double)(left - 40)
//                                    || mouseY < (double)(top + handler.invOffset - 35)
//                                    || mouseX >= (double)(left + this.imageWidth + 40)
//                                    || mouseY >= (double)(top + handler.invOffset + 37);
            boolean backpackRender = // BOUNDS OF BACKPACK RENDER --- INSET 5 FOR EASIER DROPPING
                        mouseX < (double)(left + 45)
                                    || mouseY < (double)(top + 15)
                                    || mouseX >= (double)(left + this.imageWidth - 45)
                                    || mouseY >= (double)(top + this.imageHeight);

            return inventory && backpackRender;// && backpackSlots;
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return super.mouseClicked(mouseX, mouseY, button);
      }
}
