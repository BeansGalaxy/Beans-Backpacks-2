package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.inventory.BackpackTooltip;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Iterator;

public class ClientBackpackTooltip implements ClientTooltipComponent {
      public static final int MAX_DISPLAY = 34;
      private final NonNullList<ItemStack> itemStacks;
      private final int size;
      private final int columns;
      private final int rowLimit;

      public ClientBackpackTooltip(BackpackTooltip tooltip) {
            this.itemStacks = tooltip.itemStacks;
            int size = itemStacks.size();
            this.size = Math.min(size, MAX_DISPLAY);
            this.columns = Mth.ceil(Math.sqrt(this.size + 1)) + 1;
            this.rowLimit = size > 28 ? 3 : 2;
      }

      @Override
      public int getHeight() {
            int row = Mth.ceil(size / (float) columns);
            return Math.min(row, rowLimit + 1) * 17;
      }

      @Override
      public int getWidth(Font font) {
            return columns * 17 - 1;
      }

      @Override
      public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics gui) {
            Minecraft minecraft = Minecraft.getInstance();
            int spacing = 17;

            int i = 0;
            Iterator<ItemStack> iterator = itemStacks.iterator();
            while (iterator.hasNext() && i < MAX_DISPLAY) {
                  ItemStack stack = iterator.next();
                  if (stack.isEmpty()) continue;

                  int row = i / columns;
                  int column = i % columns;
                  if (row > rowLimit) {
                        column += (row - rowLimit) * columns;
                        row = rowLimit;
                  }

                  int x = mouseX + column * spacing + 8;
                  int y = mouseY + row * spacing + 6;
                  int z = 300 - (column + row) * 2;

                  if (row == rowLimit) {
                        float row2 = size - 1 - (columns * rowLimit);
                        if (row2 >= columns) {
                              double progress = column / row2;
                              double ease = Math.lerp(progress, Math.sin((progress * Math.PI) / 2), Math.min(1, (row2 - columns + 2) / 8));
                              x = Mth.ceil(Math.lerp(mouseX + 8, mouseX + (columns * spacing) - 9, ease));
                              y += (i + (itemStacks.size() % 2)) % 2;
                              z -= column * 12;
                        }
                        int light = 15728880 / Mth.clamp(column / 2, 1, 6);
                        renderItem(minecraft, gui, stack, x, y, z, light);
                        renderItemDecorations(gui, font, stack, x - 8, y - 8, z);
                  }
                  else {
                        renderItem(minecraft, gui, stack, x, y, z, 15728880);
                        renderItemDecorations(gui, font, stack, x - 8, y - 8, z);
                  }
                  i++;
            }
      }

      public void renderItemDecorations(GuiGraphics gui, Font $$0, ItemStack $$1, int x, int y, int z) {
            if (!$$1.isEmpty()) {
                  PoseStack pose = gui.pose();
                  pose.pushPose();
                  pose.translate(0.0F, 0.0F, z + 10);
                  float value = (z / 300f) * (z / 300f) * (z / 300f);
                  if ($$1.getCount() != 1) {
                        String $$5 = String.valueOf($$1.getCount());
                        gui.drawString($$0, $$5, x + 19 - 2 - $$0.width($$5), y + 6 + 3, new Color(value, value, value).getRGB(), true);
                  }
                  else if ($$1.isBarVisible()) {
                        int $$6 = $$1.getBarWidth();
                        int $$7 = $$1.getBarColor();
                        int barX = x + 2;
                        int barY = y + 13;
                        Color barColor = new Color($$7);
                        gui.fill(barX, barY, barX + 13, barY + 2, new Color(0, 0, 0, (int)((z / 300f) * value * 255)).getRGB());
                        gui.fill(barX, barY, barX + $$6, barY + 1, new Color((int)(barColor.getRed() * value), (int)(barColor.getGreen() * value), (int)(barColor.getBlue() * value), 255).getRGB() | -16777216);
                  }
                  pose.popPose();
            }
      }

      private void renderItem(Minecraft minecraft, GuiGraphics gui, ItemStack stack, int x, int y, int z, int light) {
            PoseStack pose = gui.pose();
            pose.pushPose();
            BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
            pose.translate(x, y, z);

            try {
                  pose.mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                  pose.scale(16.0F, 16.0F, 16.0F);
                  boolean $$8 = !model.usesBlockLight();
                  if ($$8) {
                        Lighting.setupForFlatItems();
                  }

                  minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, gui.bufferSource(), light, OverlayTexture.NO_OVERLAY, model);
                  if (!model.isGui3d()) {
                        pose.translate(1/16f, -1/16f, -1/16f);
                        minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, gui.bufferSource(), 0, OverlayTexture.NO_OVERLAY, model);
                  }

                  gui.flush();
                  if ($$8) {
                        Lighting.setupFor3DItems();
                  }
            } catch (Throwable var12) {
                  CrashReport $$10 = CrashReport.forThrowable(var12, "Rendering item");
                  CrashReportCategory $$11 = $$10.addCategory("Item being rendered");
                  $$11.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                  $$11.setDetail("Item Damage", () -> String.valueOf(stack.getDamageValue()));
                  $$11.setDetail("Item NBT", () -> String.valueOf(stack.getTag()));
                  $$11.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                  throw new ReportedException($$10);
            }

            pose.popPose();
      }
}
