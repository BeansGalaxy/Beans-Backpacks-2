package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.inventory.BackpackTooltip;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
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
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Iterator;

public class ClientBackpackTooltip implements ClientTooltipComponent {
      private static final int MAX_DISPLAY = 60;
      private static final int SPACING = 18;
      private final NonNullList<ItemStack> itemStacks;
      private final Minecraft minecraft;
      private final int size;
      private final int columns;
      private final int rows;

      public ClientBackpackTooltip(BackpackTooltip tooltip) {
            this.itemStacks = tooltip.itemStacks;
            this.minecraft = Minecraft.getInstance();
            this.size = Math.min(itemStacks.size(), MAX_DISPLAY);
            double size = this.size;

            double pow = Math.log(size / 2) + 1;
            if (this.size < 4) {
                  columns = Mth.ceil(Math.sqrt(this.size));
                  rows = Mth.ceil(size / columns);
            }
            else if (this.size < 9) {
                  rows = Mth.floor(Math.sqrt(this.size));
                  columns = Mth.ceil(size / rows);
            }
            else {
                  rows = pow > 3
                              ? 3
                              : Mth.floor(pow);

                  int floor = Mth.floor((size / rows + 6) / 2);
                  columns = floor > 9
                              ? 9
                              : floor - (floor + 1) % 2;
            }
      }

      @Override
      public int getHeight() {
            return -2;
      }

      @Override
      public int getWidth(Font font) {
            return 0;
      }

      @Override
      public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics gui) {
            int width = gui.guiWidth();
            int height = gui.guiHeight() / 2 - 1;
            int w = columns * SPACING;
            int x1;
            int x2;

            if (columns > 4) {
                  x1 = Mth.floor((width - w) / 2.0) - 1;
                  x2 = Mth.ceil((width + w) / 2.0);
            }
            else {
                  double offset = size > 4 ? 2.5 : 1.5;
                  x1 = Mth.ceil((width / 2.0) - (SPACING * offset)) - 1;
                  x2 = x1 + w + 1;
            }

            int y1 = height + 1;
            int y2 = height + SPACING * rows + 1;

            int bgColor = 0xF8110211;
            gui.fill(x1 - 1, y1 - 1, x2 + 2, y2 + 1, bgColor);
            gui.hLine(x1, x2, y1 - 2, bgColor);
            gui.hLine(x1, x2, y2 + 1, bgColor);

            int topColor = 0xFF25015c;
            int botColor = 0xFF190133;
            gui.hLine(x1, x2, y1 - 1, topColor);
            gui.fillGradient(x1, y1, x1 + 1, y2, topColor, botColor);
            gui.fillGradient(x2, y1, x2 + 1, y2, topColor, botColor);
            gui.hLine(x1, x2, y2, botColor);

            Iterator<ItemStack> iterator = itemStacks.iterator();

            int overflow = size - (rows * columns);
            int remaining = size;
            for (int i = 0; remaining > 0; i++) {
                  int count;
                  if (rows == i + 1) {
                        count = remaining;
                  }
                  else if (i == 0) {
                        count = rows == 3
                                    ? columns + Mth.floor((size - columns * rows) / 9.0 - 1)
                                    : columns;
                  }
                  else {
                        double progress = (i) / (rows - 1.0);
                        double lerp = Mth.lerp(overflow, 0, progress * 0.5);
                        int add = Mth.floor(lerp);
                        count = columns + add;
                  }
                  renderRow(gui, font, iterator, Math.max(count, this.columns), x1 + 2, height + (SPACING * i) + 2, 15728880);
                  remaining -= count;
            }

            gui.fill(x1 + 2, y1 + 1, x1 + 18, y1 + 17, 500, 0x66FFFFFF);

      }

      public void renderRow(GuiGraphics gui, Font font, Iterator<ItemStack> stacks, int columns, int leftPos, int topPos, int light) {
            int i = 0;
            while (stacks.hasNext() && i < columns) {
                  ItemStack stack = stacks.next();
                  if (stack.isEmpty()) continue;

                  int column = i % columns;
                  int x ;
                  if (columns > this.columns) {
                        double linear = (double) column / (columns - 1);
                        double lerp = Mth.lerp(this.columns - 1, 0, linear);
                        x = Mth.ceil(lerp * SPACING);
                  }
                  else x = Mth.ceil(column * SPACING);


                  x += leftPos + 8;
                  int y = topPos + 8;
                  int z = -i * 12 + 400;
                  renderItem(minecraft, gui, stack, x, y, z, light);
                  renderItemDecorations(gui, font, stack, x, y, z);
                  i++;
            }
      }

      public void renderItemDecorations(GuiGraphics gui, Font $$0, ItemStack $$1, int x, int y, int z) {
            if (!$$1.isEmpty()) {
                  PoseStack pose = gui.pose();
                  pose.pushPose();
                  pose.translate(0.0F, 0.0F, z + 10);
                  if ($$1.getCount() != 1) {
                        String $$5 = String.valueOf($$1.getCount());
                        gui.drawString($$0, $$5, x + 9 - $$0.width($$5), y + 1, 0xFFFFFFFF, true);
                  }
                  else if ($$1.isBarVisible()) {
                        int barColor = $$1.getBarColor();
                        int barX = x - 6;
                        int barY = y + 5;
                        gui.fill(barX, barY, barX + 13, barY + 2, 0xFF000000);
                        gui.fill(barX, barY, barX + $$1.getBarWidth(), barY + 1, barColor | -16777216);
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
