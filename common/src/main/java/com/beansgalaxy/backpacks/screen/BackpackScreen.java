package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

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
            Traits.LocalData traits = handler.backpackInventory.getTraits();
            int maxStacks = traits.maxStacks();
            boolean empty = traits.isEmpty();
            if (empty || maxStacks == 0 || handler.owner.isRemoved()) {
                  onClose();
                  return;
            }
            renderBackground(ctx);
            super.render(ctx, mouseX, mouseY, delta);
            renderTooltip(ctx, mouseX, mouseY);
            menu.updateSlots();
      }

      @Override
      protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            int j = this.handler.invOffset + topPos;
            context.blit(TEXTURE, leftPos, j - 123, 0, 0, 0, imageWidth, imageHeight, 256, 256);
            drawBackpack(context, width / 2, j, 202, this.handler.mirror, mouseX, mouseY);

            for (MenuSlot backpackSlot : menu.backpackSlots) {
                  if (MenuSlot.State.HIDDEN.equals(backpackSlot.state)) continue;

                  int x = backpackSlot.x + leftPos - 1;
                  int y = backpackSlot.y + topPos - 1;
                  int z = 200;
                  int color = 0x44777777;
                  context.fill(x, y, x + MenuSlot.SPACING, y + MenuSlot.SPACING, z, color);
            }

            if (minecraft.options.renderDebug && handler.owner instanceof EntityEnder ender) {
                  UUID placedBy = ender.getPlacedBy();
                  UUID uuid = handler.viewer.getUUID();
                  if (Objects.equals(placedBy.toString(), uuid.toString())) {
                        HashSet<EnderStorage.PackagedLocation> enderLocations = BackData.get(handler.viewer).getEnderLocations();
                        int i = 4;
                        for (EnderStorage.PackagedLocation location : enderLocations) {
                              MutableComponent component = location.toComponent();
                              context.drawString(this.font, component, 3, i, -1000, false);
                              i += 10;
                        }
                  }
            }
      }

      private void drawBackpack(GuiGraphics context, int x, int y, int scale, Backpack entity, int mouseX, int mouseY) {
            context.pose().pushPose();
            context.enableScissor(x - 80, y - 220, x + 80, y + 36);
            context.pose().translate(x, y + 37, 70);
            int center = leftPos + imageWidth / 2;
            int abs = (mouseX - center) * (mouseX - center);

            double pro = abs / 25000.0;

            double rot = pro == 0
                        ? 0
                        : pro == 1
                        ? 1
                        : pro < 0.5 ? Math.pow(2, 20 * pro - 10) / 2
                        : (2 - Math.pow(2, -20 * pro + 10)) / 2;

            double sign = mouseX - center < 0 ? -1 : 1;
            rot *= sign;

            int i = (mouseY - topPos);
            context.pose().mulPose(Axis.XP.rotationDegrees((i == 0 ? 1 : -i) / 25f));
            context.pose().mulPose(Axis.YP.rotationDegrees((float) ((rot * 140) + (pro * sign * 20))));
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
            boolean backpackSlots = // BOUNDS OF MAX BACKPACK SLOTS
                        mouseX < (double)(left - 10)
                                    || mouseY < (double)(top + handler.invOffset - 35)
                                    || mouseX >= (double)(left + this.imageWidth + 10)
                                    || mouseY >= (double)(top + handler.invOffset + 37);
            boolean backpackRender = // BOUNDS OF BACKPACK RENDER --- INSET 5 FOR EASIER DROPPING
                        mouseX < (double)(left + 45)
                                    || mouseY < (double)(top + 15)
                                    || mouseX >= (double)(left + this.imageWidth - 45)
                                    || mouseY >= (double)(top + this.imageHeight);

            return inventory && backpackRender && backpackSlots;
      }
}
