package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Iterator;

public class InfoWidget implements Renderable, GuiEventListener, NarratableEntry {
      private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/info_tab.png");
      public ImageButton recipeButton;
      private Minecraft minecraft;
      private int height;
      private int leftPos;
      private int topPos;
      private boolean focused = false;
      private Runnable onClick;
      private Tab selected = Tab.BACKPACK;
      public NonNullList<InfoButton> buttons = NonNullList.create();
      public HomeButton homeButton;

      public int updateScreenPosition(int i, boolean visible, int width, int imageWidth) {
            homeButton.setVisible(!visible);
            if (visible && focused) {
                  toggleFocus();
            }
            else i = ((width - imageWidth) / 2) + (focused ? 60 : 0);


            this.leftPos = i;
            recipeButton.setPosition(this.leftPos + 104, this.height / 2 - 22);
            updateButtonPositions(leftPos);
            return i;
      }

      public void updateButtonPositions(int leftPos) {
            int index = 1;
            for (InfoButton button : buttons) {
                  button.setPosition(leftPos - (23 * index), topPos);
                  index++;
            }
      }

      public enum Tab {
            BACKPACK    (0),
            ENDER       (1),
            POT         (2),
            CAULDRON    (3);

            final int index;
            Tab(int i) {
                  index = i;
            }
      }

      @Override
      public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
            if (focused) {
                  guiGraphics.blit(TEXTURE, leftPos - 166, topPos + 22, 0, 1, 53, 167, 144, 256, 256);
                  MutableComponent title = Component.translatable("tooltip.beansbackpacks.help." + selected.name().toLowerCase() + "_title");
                  FormattedCharSequence titleSequence = title.withStyle(ChatFormatting.BOLD).getVisualOrderText();
                  int titleY = this.topPos + 29;
                  drawCenteredText(guiGraphics, titleSequence, titleY);
                  drawCenteredLine(guiGraphics, minecraft.font.width(titleSequence), titleY + 10);
                  NonNullList<String> keys = NonNullList.create();
                  int buffer = 0;
                  for (int j = 0; j < 16; j++) {
                        String key = "tooltip.beansbackpacks.help." + selected.name().toLowerCase() + (j);
                        if (Language.getInstance().has(key)) {
                              while (buffer > 0) {
                                    keys.add("");
                                    buffer--;
                              }
                              keys.add(key);
                        }
                        else buffer++;
                  }

                  String keyBind = "§0" + Tooltip.getKeyBinding().getTranslatedKeyMessage().getString()
                              .replace("Left ", "L")
                              .replace("Right ", "R")
                              .replace("Control", "Ctrl");
                  String useKey = "§0" + minecraft.options.keyUse.getTranslatedKeyMessage().getString()
                              .replace("Right Button", "RClick")
                              .replace("Left Button", "LClick")
                              .replace("Left ", "L")
                              .replace("Right ", "R");

                  int stringY = 43;
                  int hLineY = -1;
                  int width = -1;
                  FormattedCharSequence visualOrderText = null;
                  Iterator<String> iterator = keys.iterator();
                  while (iterator.hasNext()) {
                        String key = iterator.next();
                        if (!Language.getInstance().has(key) && iterator.hasNext()) {
                              if (visualOrderText == null && hLineY == -1 && width == -1) {
                                    stringY += 2;
                                    continue;
                              }
                              if (visualOrderText != null) {
                                    width = minecraft.font.width(visualOrderText);
                                    visualOrderText = null;
                                    hLineY = stringY + 1;
                                    stringY += 5;
                                    continue;
                              }
                              hLineY += 2;
                              stringY += 4;
                        }
                        else {
                              if (visualOrderText != null && hLineY != -1)
                                    drawCenteredLine(guiGraphics, width, this.topPos + hLineY);
                              visualOrderText = Component.translatableWithFallback(key, "", keyBind, useKey).getVisualOrderText();
                              drawCenteredText(guiGraphics, visualOrderText, this.topPos + stringY);
                              stringY += 9;
                        }
                  }
                  int x = leftPos + 4;
                  int y = topPos + 4;
                  guiGraphics.renderFakeItem(Services.REGISTRY.getLeather().getDefaultInstance(), x -= 23, y);
                  guiGraphics.renderFakeItem(Services.REGISTRY.getEnder().getDefaultInstance(), x -= 23, y);
                  guiGraphics.renderFakeItem(Items.DECORATED_POT.getDefaultInstance(), x -= 23, y);
                  guiGraphics.renderFakeItem(Items.CAULDRON.getDefaultInstance(), x - 22, y - 1);
            }
      }

      private void drawCenteredText(GuiGraphics gui, FormattedCharSequence text, int topPos) {
            Font font = minecraft.font;
            gui.drawString(font, text, (this.leftPos - 79 + 2) - (font.width(text) / 2), topPos, 4210752, false);

      }

      private void drawCenteredLine(GuiGraphics gui, int width, int topPos) {
            int left = (this.leftPos - 79 + 2) - (width / 2);
            int average = (100 - width) / 10;
            gui.hLine(left + width + average, left - average, topPos, 0xff8b8b8b);

      }

      @Override
      public void setFocused(boolean b) {
            focused = b;
      }

      @Override
      public boolean isFocused() {
            return focused;
      }

      @Override @NotNull
      public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
      }

      @Override
      public void updateNarration(NarrationElementOutput narrationElementOutput) {

      }

      public void init(int height, int leftPos, int topPos, Minecraft minecraft, InventoryMenu menu, Runnable onClick) {
            this.minecraft = minecraft;
            this.height = height;
            this.leftPos = leftPos;
            this.topPos = topPos + 1;
            this.onClick = onClick;
            focused = false;
            this.homeButton = new HomeButton(this.leftPos, this.topPos, this, (button) -> {
                  onButtonClick(Tab.BACKPACK);
            });
            buttons.clear();
            buttons.add(homeButton);
            buttons.add(new InfoButton(2, this.leftPos, this.topPos, (button) -> {
                  onButtonClick(Tab.ENDER);
            }));
            buttons.add(new InfoButton(3, this.leftPos, this.topPos, (button) -> {
                  onButtonClick(Tab.POT);
            }));
            buttons.add(new InfoButton(4, this.leftPos, this.topPos, (button) -> {
                  onButtonClick(Tab.CAULDRON);
            }));
      }

      private void onButtonClick(Tab selected) {
            if (this.selected == selected) {
                  setSelected(Tab.BACKPACK);
                  this.toggleFocus();
            }
            else {
                  setSelected(selected);
            }
            onClick.run();
      }

      public void setSelected(Tab selected) {
            this.selected = selected;
      }

      public void toggleFocus() {
            setFocused(!focused);
            for (InfoButton button : buttons) {
                  if (button != homeButton)
                        button.setVisible(focused);
                  if (!focused)
                        button.setFocused(false);
            }
      }

      public static class InfoButton extends ImageButton {
            public InfoButton(int index, int leftPos, int topPos, OnPress o) {
                  super(leftPos - (23 * index), topPos, 24, 25, 24 * index, 0, 25, TEXTURE, o);
                  init();
            }

            public void init() {
                  visible = false;
            }

            public void setVisible(boolean visible) {
                  this.visible = visible;
            }

            @Override
            public void setFocused(boolean focused) {
                  super.setFocused(focused);
            }
      }

      public static class HomeButton extends InfoButton {
            private final InfoWidget parent;

            public HomeButton(int leftPos, int topPos, InfoWidget parent, OnPress o) {
                  super(1, leftPos, topPos, o);
                  this.parent = parent;
            }

            @Override
            public void init() {

            }

            @Override
            public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
                  if (!parent.focused)
                        this.renderTexture($$0, this.resourceLocation, this.getX(), this.getY(), 0, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                  else
                        super.renderWidget($$0, $$1, $$2, $$3);

            }
      }
}
