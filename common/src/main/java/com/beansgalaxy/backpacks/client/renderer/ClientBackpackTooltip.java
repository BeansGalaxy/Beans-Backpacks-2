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
      public static final int MAX_DISPLAY = 32;
      private final NonNullList<ItemStack> itemStacks;
      private final int size;
      private final int columns;
      private final int rowLimit;

      public ClientBackpackTooltip(BackpackTooltip tooltip) {
            this.itemStacks = tooltip.itemStacks;
            int size = itemStacks.size();
            this.size = Math.min(size, MAX_DISPLAY);
            this.columns = Mth.ceil(Math.sqrt(this.size + 1)) + 1;
            this.rowLimit = size > MAX_DISPLAY ? 3 : 2;
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
            int i = 0;
            //gui.fillGradient(mouseX, mouseY - 2, mouseX + 16, mouseY + 14, 0xFF260051, 0xFF110040);
//            int topColor = 0xFF260051;
//            int botColor = 0xFF160038;
//            int height = getHeight();
//            for (int j = 0; j < size / columns - 1; j++)
//                  hline(mouseX, mouseY, gui, topColor, botColor, height, j);
//
//            for (int j = 0; j < columns - 1; j++)
//                  vLine(mouseX, mouseY, gui, topColor, botColor, height - 16, j);

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

                  int x = mouseX + column * 17 + 8;
                  int y = mouseY + row * 17 + 6;
                  int z = -i * 2 + 150;

                  if (row == rowLimit) {
                        float row2 = size - 1 - (columns * rowLimit);
                        if (row2 >= columns) {
                              double progress = column / row2;
                              double ease = Math.lerp(progress, Math.sin((progress * Math.PI) / 2), Math.min(1, (row2 - columns + 2) / 8));
                              x = Mth.ceil(Math.lerp(mouseX + 8, mouseX + (columns * 17) - 9, ease));
                              y += (i + (itemStacks.size() % 2)) % 2;
                        }
                        //int light = (int) (15728880 * Math.max(1 - progress, 0.2f));
                        int light = 15728880 / Mth.clamp(column / 2, 1, 6);
                        renderItem(minecraft, gui, stack, x, y, z, light);
                        renderItem(minecraft, gui, stack, x + 1, y + 1, z - 1, 0);
                  }
                  else renderItem(minecraft, gui, stack, x, y, z, 15728880);
                  i++;
            }
      }

      private void hline(int mouseX, int mouseY, GuiGraphics gui, int topColor, int botColor, int height, int row) {
            int y = (row * 17) + 14;
            Color top = new Color(topColor);
            Color bot = new Color(botColor);
            float progress = Mth.clamp((float) y / height, 0, 1);
            int r = (int) Math.lerp(top.getRed(),     bot.getRed(),     progress);
            int g = (int) Math.lerp(top.getGreen(),   bot.getGreen(),   progress);
            int b = (int) Math.lerp(top.getBlue(),    bot.getBlue(),    progress);
            Color color = new Color(r, g, b, 255);
            gui.hLine(mouseX, mouseX + (columns * 17) - 2, mouseY + y, color.getRGB());
      }

      private void vLine(int mouseX, int mouseY, GuiGraphics gui, int topColor, int botColor, int height, int column) {
            int x = mouseX + (column * 17) + 16;
            int y = mouseY - 2;
            gui.fillGradient(x, y, x + 1, y + height, topColor, botColor);
      }

      private void renderItem(Minecraft minecraft, GuiGraphics gui, ItemStack stack, int x, int y, int z, int light) {
            PoseStack pose = gui.pose();
            pose.pushPose();
            BakedModel $$7 = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
            pose.translate(x, y, z);

            try {
                  pose.mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                  pose.scale(16.0F, 16.0F, 16.0F);
                  boolean $$8 = !$$7.usesBlockLight();
                  if ($$8) {
                        Lighting.setupForFlatItems();
                  }

                  minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, gui.bufferSource(), light, OverlayTexture.NO_OVERLAY, $$7);
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
