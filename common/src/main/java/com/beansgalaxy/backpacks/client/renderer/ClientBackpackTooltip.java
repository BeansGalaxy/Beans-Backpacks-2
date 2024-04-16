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
      private static final int MAX_DISPLAY = 48;
      private static final int SPACING = 17;
      private final NonNullList<ItemStack> itemStacks;
      private final Minecraft minecraft;
      private final int size;
      private final int columns;
      private final int rows;

      public ClientBackpackTooltip(BackpackTooltip tooltip) {
            this.itemStacks = tooltip.itemStacks;
            this.minecraft = Minecraft.getInstance();
            this.size = Math.min(45, itemStacks.size());
            double sqrt = Math.sqrt(this.size);

            int columns = Math.min(Mth.ceil(sqrt + 6) / 2, Mth.ceil(sqrt));
            this.columns = columns > 5
                        ? columns
                        : size < 5
                        ? size
                        : (columns + 5) / 2;
            int rows = Mth.ceil(((float) size) / this.columns);
            this.rows = rows > 2
                        ? (rows + 2) / 2
                        : rows;

      }

      @Override
      public int getHeight() {
            return rows * SPACING;
      }

      @Override
      public int getWidth(Font font) {
            return columns * SPACING - 1;
      }

      @Override
      public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics gui) {
            Iterator<ItemStack> iterator = itemStacks.iterator();
            int rows = this.rows;
            int overflow = size - (rows * columns);
            int remaining = size;
            for (int i = 0; remaining > 0; i++) {
                  int count;
                  if (rows == i + 1) {
                        count = remaining;
                  }
                  else {
                        double progress = (i) / (rows - 1.0);
                        double lerp = Mth.lerp(overflow, 0, progress * 0.5);
                        int add = Mth.floor(lerp);
                        count = columns + add;
                  }
                  renderRow(gui, font, iterator, Math.max(count, this.columns), mouseX, mouseY + (SPACING * i), 15728880);
                  remaining -= count;
            }

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
                  int z = -i * 12 + 200;
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
                        gui.drawString($$0, $$5, x + 9 - $$0.width($$5), y + 1, 0xFFDDDDDD, true);
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
