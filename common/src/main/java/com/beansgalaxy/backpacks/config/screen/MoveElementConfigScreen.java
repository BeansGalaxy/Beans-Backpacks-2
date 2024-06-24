package com.beansgalaxy.backpacks.config.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class MoveElementConfigScreen extends Screen {
      private final ResourceLocation background;
      private final Screen lastScreen;
      private final int bgU;
      private final int bgV;
      private final int bgWidth;
      private final int bgHeight;
      private final BiConsumer<Integer, Integer> onSave;
      private final int elementW;
      private final int elementH;
      private int elementX;
      private int elementY;
      private int topPos;
      private int leftPos;

      public MoveElementConfigScreen(Screen lastScreen, ResourceLocation background, BiConsumer<Integer, Integer> onSave, int elementX, int elementY, int elementW, int elementH, int bgWidth, int bgHeight, int bgU, int bgV) {
            super(Component.empty());
            this.lastScreen = lastScreen;
            this.background = background;
            this.bgU = bgU;
            this.bgV = bgV;
            this.onSave = onSave;
            this.elementW = elementW;
            this.elementH = elementH;
            this.bgWidth = bgWidth;
            this.bgHeight = bgHeight;
            this.elementX = elementX;
            this.elementY = elementY;
      }

      @Override
      public boolean mouseDragged(double x1, double y1, int i, double x2, double y2) {
            if (i == 0) {
                  elementX = (int) (x1 - leftPos) - elementW / 2;
                  elementY = (int) (y1 - topPos) - elementH / 2;
            }
            return super.mouseDragged(x1, y1, i, x2, y2);
      }

      @Override
      protected void init() {
            super.init();

            this.leftPos = (int) (bgWidth / -2.0 + width / 2.0);
            this.topPos = (int) (bgHeight / -2.0 + height / 2.0);

            int center = width / 2;
            Button save = Button.builder(Component.translatable("screen.beansbackpacks.move_element.save_and_close"), in -> {
                  onSave.accept(elementX, elementY);
                  onClose();
            }).bounds(center + 5, height - 26, 80, 20).build();

            Button exit = Button.builder(Component.translatable("screen.beansbackpacks.move_element.cancel_and_exit"), in -> {
                  onClose();
            }).bounds(center - 85, height - 26, 80, 20).build();

            addRenderableWidget(save);
            addRenderableWidget(exit);
      }

      @Override
      public void onClose() {
            minecraft.setScreen(lastScreen);
      }

      @Override
      public void render(GuiGraphics gui, int x, int y, float delta) {
            super.renderBackground(gui);
            int eleX = leftPos + elementX;
            int eleY = topPos + elementY;
            gui.fill(eleX, eleY, elementW + eleX, elementH + eleY, 300, 0xFFEE3333);
            gui.blit(background, leftPos, topPos, bgU, bgV, bgWidth, bgHeight);
            super.render(gui, x, y, delta);
      }

      public static class Builder {
            private BiConsumer<Integer, Integer> onSave = (x, y) -> {};
            private ResourceLocation background = null;
            private int elementX = 0;
            private int elementY = 0;
            private int elementW = 1;
            private int elementH = 1;
            private int bgW = 0;
            private int bgH = 0;
            private int bgU = 0;
            private int bgV = 0;

            public static Builder create() {
                  return new Builder();
            }

            public Builder elementPos(int x, int y) {
                  elementX = x;
                  elementY = y;
                  return this;
            }

            public Builder elementSize(int width, int height) {
                  elementW = width;
                  elementH = height;
                  return this;
            }

            public Builder backgroundSize(int width, int height) {
                  bgW = width;
                  bgH = height;
                  return this;
            }

            public Builder backgroundUV(int x, int y) {
                  bgU = x;
                  bgV = y;
                  return this;
            }

            public Builder background(ResourceLocation background) {
                  this.background = background;
                  return this;
            }

            public Builder onSave(BiConsumer<Integer, Integer> onClose) {
                  this.onSave = onClose;
                  return this;
            }

            public MoveElementConfigScreen build(Screen lastScreen) {
                  return new MoveElementConfigScreen(lastScreen, background, onSave, elementX, elementY, elementW, elementH, bgW, bgH, bgU, bgV);
            }
      }
}
