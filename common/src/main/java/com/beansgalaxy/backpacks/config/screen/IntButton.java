package com.beansgalaxy.backpacks.config.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntButton extends Button {
      private final Consumer<Integer> onClose;
      private final Consumer<Integer> onEnter;
      private final Supplier<Integer> getValue;
      public int storedValue = 0;
      private boolean isFirstEdit = true;

      protected IntButton(int x, int y, int w, int h, OnPress onPress, Consumer<Integer> onClose, Consumer<Integer> onEnter, Supplier<Integer> getValue, CreateNarration narration) {
            super(x, y, w, h, Component.empty(), onPress, narration);
            this.onClose = onClose;
            this.onEnter = onEnter;
            this.getValue = getValue == null ? () -> storedValue : getValue;
      }

      public static Builder builder(OnPress onPress, @Nullable Supplier<Integer> getValue) {
            return new Builder(onPress, getValue);
      }

      @Override
      public void onPress() {
            isFirstEdit = true;
            super.onPress();
      }

      @Override
      public boolean keyPressed(int i, int j, int k) {
            switch (i) {
                  case 259 -> { // DELETE
                        if (storedValue == 0) {
                              close();
                        }
                        storedValue /= 10;
                  }
                  case 45 -> { // MINUS
                        storedValue *= -1;
                  }
                  case 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 -> {
                        if (storedValue < 1000000 && storedValue > -1000000) {
                              int n = i - 48;
                              storedValue = storedValue * 10 + n;
                        }
                  }
                  case 257 -> { // ENTER
                        if (!isFirstEdit)
                              onEnter.accept(storedValue);
                        close();
                  }
            }
            isFirstEdit = false;
            return super.keyPressed(i, j, k);
      }

      private void close() {
            onClose.accept(storedValue);
            setFocused(false);
      }

      @Override
      public Component getMessage() {
            return isFocused() ?
                        Component.literal("[" + (isFirstEdit ? getValue.get() : storedValue) + "]") :
                        Component.literal(String.valueOf(getValue.get()));
      }

      public static class Builder {
            private final OnPress onPress;
            private final Supplier<Integer> getValue;
            private Consumer<Integer> onClose = in -> {};
            private Consumer<Integer> onEnter = in -> {};
            private CreateNarration narration = Button.DEFAULT_NARRATION;
            private int x = 0;
            private int y = 0;
            private int w = 0;
            private int h = 0;

            private Builder(OnPress onPress, Supplier<Integer> getValue) {
                  this.onPress = onPress;
                  this.getValue = getValue;
            }

            public Builder bounds(int x, int y, int w, int h) {
                  this.x = x;
                  this.y = y;
                  this.w = w;
                  this.h = h;
                  return this;
            }

            public Builder onClose(Consumer<Integer> onClose) {
                  this.onClose = onClose;
                  return this;
            }

            public Builder onEnter(Consumer<Integer> onEnter) {
                  this.onEnter = onEnter;
                  return this;
            }

            public IntButton build() {
                  return new IntButton(x, y, w, h, onPress, onClose, onEnter, getValue, narration);
            }
      }
}
