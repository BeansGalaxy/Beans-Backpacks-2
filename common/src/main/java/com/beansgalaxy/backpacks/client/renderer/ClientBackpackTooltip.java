package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.PosAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.inventory.BackpackTooltip;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import java.lang.Math;
import org.joml.Matrix4f;

import java.util.Iterator;

public class ClientBackpackTooltip implements ClientTooltipComponent {
      private static final int MAX_DISPLAY = 60;
      private static final int SPACING = 18;
      private final NonNullList<ItemStack> itemStacks;
      private final Minecraft minecraft;
      private final int size;
      private final boolean isCuriosMenu;
      private final TooltipType tooltipType;

      public ClientBackpackTooltip(BackpackTooltip tooltip) {
            this.itemStacks = tooltip.itemStacks;
            this.minecraft = Minecraft.getInstance();
            this.size = Math.min(itemStacks.size(), MAX_DISPLAY);
            this.tooltipType = Constants.CLIENT_CONFIG.tooltip_style.get();
            this.isCuriosMenu = Tooltip.isCuriosMenu();
      }

      @Override
      public int getHeight() {
            return isCuriosMenu && TooltipType.INTEGRATED != tooltipType
                        ? -17
                        : -2;
      }

      @Override
      public int getWidth(Font font) {
            return 0;
      }

      @Override
      public void renderImage(Font font, int cursorX, int cursorY, GuiGraphics gui) {
            if (!isCuriosMenu) {
                  cursorX += 10000;
                  cursorY += 10000;
            }

            switch (tooltipType) {
                  case VANILLA -> renderVanilla(font, gui, cursorX, cursorY);
                  case INTEGRATED -> renderLarge(font, gui, cursorX + 2, cursorY - 6);
                  default -> renderCompact(font, gui, cursorX, cursorY);
            }
      }

      private void renderVanilla(Font font, GuiGraphics gui, int mouseX, int mouseY) {
            Iterator<ItemStack> stacks = itemStacks.iterator();
            ResourceLocation bundleTexture = new ResourceLocation("textures/gui/container/bundle.png");
            LocalPlayer player = minecraft.player;
            BackData backData = BackData.get(player);
            boolean hasSpace = backData.getBackpackInventory().spaceLeft() > 0;

            int limitC = 5;
            double sudoSize = size + (hasSpace ? 1.0 : 0);
            double sqrt = Math.sqrt(sudoSize);
            int columns = Mth.ceil(sqrt > limitC
                        ? sqrt - (sqrt - limitC) / 3
                        : sqrt
            );

            int rows = Mth.ceil(sqrt);
            int leftPos = mouseX + 10;
            int offset = isCuriosMenu ? 5 : Mth.ceil(sudoSize / columns) * 4;
            int topPos = mouseY - offset;
            int spacing = 18;

            int w = columns * spacing;
            if (player.inventoryMenu.getCarried().isEmpty()) {
                  ItemStack item = itemStacks.get(0);
                  Component name = Constants.getName(item);
                  int textWidth = font.width(name.getVisualOrderText());
                  int textOffset = (hasSpace ? -9 : 5) + (columns > 4 ? 0 : 7);
                  int textLengthOverflow = isCuriosMenu ? 0 : textWidth - w + (12);
                  int tooLong = Math.max(textOffset, textLengthOverflow);

                  drawTooltipBox(gui, leftPos - tooLong, topPos - 28, textWidth + 5, 12, 0);
                  gui.drawString(font, name, leftPos + 3 - tooLong, topPos - 26, 0xFFFFFFFF);
            }

            int firstX = leftPos - 9;
            int firstY = topPos - 9;
            gui.blit(bundleTexture, firstX, firstY, 200, 0, 0, spacing, spacing, 128, 128);

            int x = hasSpace ? 1 : 0;
            int y = 0;
            int itemsLeft = itemStacks.size();
            while (stacks.hasNext() && y < rows) {
                  while (x < columns) {
                        int x1 = x * spacing + leftPos;
                        int y1 = y * spacing + topPos;
                        if (rows - 1 == y && x == columns - 1 && itemsLeft > 1) {
                              String text = "&" + itemsLeft;
                              int textW = font.width(text);
                              int center = x1 - (textW / 2) + 1;
                              gui.drawString(font, text, center, y1 - 3, 0xFFFFFFFF);
                        }
                        else {
                              if (stacks.hasNext()) {
                                    gui.blit(bundleTexture, x1 - 9, y1 - 9, 200, 0, 0, spacing, spacing, 128, 128);
                                    ItemStack stack = stacks.next();
                                    renderItem(minecraft, gui, stack, x1, y1, 300, false);
                                    renderItemDecorations(gui, font, stack, x1, y1, 300);
                                    itemsLeft--;
                              }
                              else {
                                    gui.blit(bundleTexture, x1 - 9, y1 - 9, 200, 0, hasSpace? 0: 40, spacing, spacing, 128, 128);
                              }
                        }
                        x++;
                  }
                  x = 0;
                  y++;
            }

            boolean empty = player.inventoryMenu.getCarried().isEmpty();
            boolean isQuickMove = backData.menusKeyDown || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
            int fillX = firstX + ((empty || isQuickMove) && hasSpace ? spacing : 0);
            gui.fill(fillX + 1, firstY + 1, fillX + 17, firstY + 17, 500, 0x78FFFFFF);

            drawTooltipBox(gui, leftPos - 12, topPos - 11, w + 5, y * spacing + 4, 0);
      }

      private void renderCompact(Font font, GuiGraphics gui, int mouseX, int mouseY) {
            double sqrt = Math.sqrt(this.size);

            int c = Math.min(Mth.ceil(sqrt + 6) / 2, Mth.ceil(sqrt));
            int columns = c > 5
                        ? c
                        : size < 5
                        ? size
                        : (c + 5) / 2;
            int r = Mth.ceil(((float) size) / columns);
            int rows;
            if (r > 2) {
                  rows = (r + 2) / 2;
                  columns += (r - rows) / 2;
            }
            else {
                  rows = r;
            }

            int offsetY = isCuriosMenu ? 15 : rows * 7;
            int maxRows = 5;
            int maxColumns = 7;
            int itemsWidth = Math.min(maxColumns, columns) * SPACING + 3;

            if (minecraft.player.inventoryMenu.getCarried().isEmpty()) {
                  ItemStack item = itemStacks.get(0);
                  Component name = Constants.getName(item);
                  int textWidth = font.width(name.getVisualOrderText());
                  int textLengthOverflow = isCuriosMenu ? 0 : textWidth - itemsWidth + 7;
                  int tooltipOffset = Math.max(0, textLengthOverflow);

                  drawTooltipBox(gui, mouseX - tooltipOffset, mouseY - 18 - offsetY, textWidth + 5, 12, 0);
                  gui.drawString(font, name, mouseX + 3 - tooltipOffset, mouseY - offsetY - 16, 0xFFFFFFFF);
            }

            drawTooltipBox(gui, mouseX - 2, mouseY - 1 - offsetY, itemsWidth, Math.min(maxRows, rows) * SPACING + 2, 0);
            drawItems(font, gui, mouseX - 1, mouseY - offsetY, rows, columns, 5, 7);
      }

      private void renderLarge(Font font, GuiGraphics gui, int mouseX, int mouseY) {
            Screen screen = minecraft.screen;
            if (!(screen instanceof EffectRenderingInventoryScreen<?>)) return;

            double size = this.size;
            int maxColumns = 9;
            int maxRows = 3;
            int rows;
            int columns;

            if (this.size < 4) {
                  columns = Mth.ceil(Math.sqrt(this.size));
                  rows = Mth.ceil(size / columns);
            }
            else if (this.size < 9) {
                  rows = Mth.floor(Math.sqrt(this.size));
                  columns = Mth.ceil(size / rows);
            }
            else {
                  double pow = Math.log(size / 2) + 1;
                  rows = pow > maxRows
                              ? maxRows
                              : Mth.floor(pow);

                  int floor = Mth.floor((size / rows + 6) / 2);
                  columns = floor > maxColumns
                              ? floor - ((floor - maxColumns) / 3)
                              : floor - (floor + 1) % 2;
            }

            PosAccessor invScreen = (PosAccessor) screen;
            int[] pos = invScreen.getPos();

            int x;
            int y = pos[1];
            int h = SPACING * rows;
            int w = Math.min(columns, maxColumns) * SPACING;

            if (columns > 4) {
                  x = pos[0] - Mth.floor((w / 2.0)) - 1;
            }
            else {
                  double offset = size > 4 ? 2.5 : 1.5;
                  x = Mth.ceil(pos[0] - (SPACING * offset)) - 1;
            }

            if (minecraft.player.inventoryMenu.getCarried().isEmpty()) {
                  if (!(screen instanceof InventoryScreen)) {
                        mouseX = x + 10;
                        mouseY = pos[1] - 15;
                  }
                  ItemStack item = itemStacks.get(0);
                  Component name = Constants.getName(item);
                  int width1 = font.width(name.getVisualOrderText());
                  drawTooltipBox(gui, mouseX - 3, mouseY - 2, width1 + 5, 12, 150);
                  gui.pose().pushPose();
                  gui.pose().translate(0, 0, 200);
                  gui.drawString(font, name, mouseX, mouseY, 0xFFFFFFFF);
                  gui.pose().popPose();
            }

            drawTooltipBox(gui, x, y, w + 1, h, 0);
            drawItems(font, gui, x, y, rows, columns, 3, maxColumns);
      }

      private void drawItems(Font font, GuiGraphics gui, int x, int y, int r, int c, int maxRows, int maxColumns) {
            Iterator<ItemStack> stacks = itemStacks.iterator();

            int itemsLeft = size;
            int totalRows = Math.min(r, maxRows);
            int extraItems = size - (r * c);
            for (int i = 0; itemsLeft > 0; i++) {
                  int count;
                  if (i == 0) {
                        count = c;
                  }
                  else if (totalRows == i + 1) {
                        count = itemsLeft;
                  }
                  else {
                        double progress = (i) / (totalRows - 1.0);
                        double lerp = Mth.lerp(extraItems, 0, progress * 0.5);
                        count = Math.max(c, c + Mth.floor(lerp));
                  }

                  for (int j = 0; stacks.hasNext() && j < count; j++) {
                        ItemStack stack = stacks.next();
                        if (stack.isEmpty()) continue;

                        int column = j % count;
                        int itemX ;
                        int columns = Math.min(c, maxColumns);
                        if (count > columns) {
                              double linear = (double) column / (count - 1);
                              double lerp = Mth.lerp(columns - 1, 0, linear);
                              itemX = Mth.ceil(lerp * SPACING);
                        }
                        else itemX = Mth.ceil(column * SPACING);


                        itemX += x + 10;
                        int itemY = y + (SPACING * i) + 9;
                        int z = -j * 12 + 400;
                        renderItem(minecraft, gui, stack, itemX, itemY, z, true);
                        renderItemDecorations(gui, font, stack, itemX, itemY, z);
                  }

                  itemsLeft -= count;
            }

            gui.fill(x + 2, y + 1, x + 18, y + 17, 500, 0x66FFFFFF);
      }

      private static void drawTooltipBox(GuiGraphics gui, int x, int y, int w, int h, int z) {
            int bgColor = 0xF0100010;
            gui.fill(x - 1, y - 1, x + w + 2, y + h + 1, z, bgColor);
            gui.fill(x, y - 1, x + w, y - 2, z, bgColor);
            gui.fill(x, y + h + 2, x + w, y + h + 1, z, bgColor);

            int topColor = 0x505000FF;
            int botColor = 0x5028007F;
            gui.fill(x, y, x + w, y - 1, z, topColor);
            gui.fillGradient(x, y, x + 1, y + h, z, topColor, botColor);
            gui.fillGradient(x + w, y, x + w + 1, y + h, z, topColor, botColor);
            gui.fill(x, y + h + 1, x + w, y + h, z, botColor);
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

      private void renderItem(Minecraft minecraft, GuiGraphics gui, ItemStack stack, int x, int y, int z, boolean drawShadows) {
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

                  minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, gui.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, model);
                  if (drawShadows && !model.isGui3d()) {
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
