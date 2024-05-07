package com.beansgalaxy.backpacks.screen.InfoTabs;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.ClientConfig;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.function.Supplier;

public class InfoWidget implements Renderable, GuiEventListener, NarratableEntry {
      private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/info_tab.png");
      private RecipeBookComponent recipeBook;
      public ImageButton recipeButton;
      private Minecraft minecraft;
      private int height;
      private int leftPos;
      private int topPos;
      private boolean focused = false;
      private Runnable onClick;
      private Tabs selected = Tabs.BACKPACK;
      public NonNullList<Optional<InfoButton>> buttons = NonNullList.create();
      public HomeButton homeButton;
      public HideButton hideButton;

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
            for (Optional<InfoButton> optional : buttons) {
                  optional.ifPresent(button -> {
                        Tabs tab = button.tab;
                        button.setPosition(leftPos + tab.offsetXY[0], topPos);
                  });
            }
      }

      @Override
      public void render(GuiGraphics ctx, int i, int i1, float v) {
            if (focused) {
                  ctx.blit(TEXTURE, leftPos - 166, topPos + 22, 0, 1, 53, 167, 144, 256, 256);
                  String lowerCase = minecraft.player.getName().plainCopy().getString().toLowerCase();
                  MutableComponent title = Component.translatable("help.beansbackpacks." + selected.name().toLowerCase() + "_title", lowerCase);
                  FormattedCharSequence titleSequence = title.withStyle(ChatFormatting.BOLD).getVisualOrderText();
                  int titleY = this.topPos + 29;
                  drawCenteredText(ctx, titleSequence, titleY);
                  drawCenteredLine(ctx, minecraft.font.width(titleSequence), titleY + 10);

                  //ctx.fill(leftPos - 152, topPos + 25, leftPos - 1, topPos + 160, 0, 0xFFEE6666);
                  Color c = selected.color;
                  int transparent = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0).getRGB();
                  ctx.fillGradient(leftPos - 152, topPos + 25, leftPos - 1, topPos + 160, 0, transparent, c.getRGB());

                  int stringY = 44;
                  int hLineY = 0;
                  Language language = Language.getInstance();
                  FormattedCharSequence visualOrderText = null;
                  for (int j = 0; j < 16; j++) {
                        String key = "help.beansbackpacks." + selected.name().toLowerCase() + (j);
                        if (!language.has(key)) {
                              if (visualOrderText != null)
                                    stringY += 2;
                              hLineY--;
                              stringY++;
                              continue;
                        }

                        boolean hLine = language.getOrDefault(key).chars().allMatch(ch -> ch == '-');
                        if (hLine && visualOrderText != null) {
                              drawCenteredLine(ctx, minecraft.font.width(visualOrderText), this.topPos + stringY + hLineY);
                              hLineY = 0;
                              stringY += 5;
                        } else {
                              MutableComponent mutableComponent = Component.translatableWithFallback(key, "", selected.getVarForLine(minecraft, j));
                              visualOrderText = mutableComponent.getVisualOrderText();
                              drawCenteredText(ctx, visualOrderText, this.topPos + stringY - 1);
                              hLineY = 0;
                              stringY += 9;
                        }
                  }

                  for (Tabs tab : Tabs.values()) {
                        if (tab.isUnlocked(minecraft)) {
                              tab.render(ctx, leftPos - 19, topPos + 4);
                        }
                  }
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

      @Override @NotNull
      public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
      }

      @Override
      public void updateNarration(NarrationElementOutput narrationElementOutput) {

      }

      public void init(int height, int leftPos, int topPos, Minecraft minecraft, RecipeBookComponent recipeBookComponent, Runnable onClick) {
            this.minecraft = minecraft;
            this.height = height;
            this.leftPos = leftPos;
            this.topPos = topPos + 1;
            this.onClick = onClick;
            this.recipeBook = recipeBookComponent;
            focused = false;

            if (Constants.CLIENT_CONFIG.menu_visibility.get().equals(MenuVisibility.HIDE_ABLE))
                  hiddenTabs = Constants.CLIENT_CONFIG.hidden_tabs.get();
            this.homeButton = new HomeButton(this, (button) -> onButtonClick(Tabs.BACKPACK));
            this.hideButton = new HideButton(this, (button) -> {
                  toggleHidden();
                  onClick.run();
            });

            updateVisible();

            buttons.clear();
            buttons.add(Optional.of(homeButton));
            for (Tabs tab : Tabs.values()) {
                  if (tab == Tabs.BACKPACK) continue;
                  buttons.add(createInfoButton(tab));
            }
      }

      public void updateVisible() {
            boolean menuEnabled = !isHidden() || focused;
            boolean RecipeBookClosed = !recipeBook.isVisible();
            boolean BeganProgress = Tabs.BACKPACK.isUnlocked(minecraft);
            homeButton.setVisible(menuEnabled && RecipeBookClosed && BeganProgress);
      }

      private Optional<InfoButton> createInfoButton(Tabs tab) {
            if (tab.isUnlocked(minecraft)) {
                  return Optional.of(new InfoButton(tab, this.leftPos, this.topPos, (button) -> {
                        onButtonClick(tab);
                  }));
            }
            else return Optional.empty();
      }

      private void onButtonClick(Tabs selected) {
            if (this.selected == selected) {
                  setSelected(Tabs.BACKPACK);
                  this.toggleFocus();
            }
            else {
                  setSelected(selected);
            }
            onClick.run();
      }

      public void setSelected(Tabs selected) {
            this.selected = selected;
      }

      public void toggleFocus() {
            setFocused(!focused);
            for (Optional<InfoButton> optional : buttons) {
                  optional.ifPresent(button -> {
                        if (button != homeButton)
                              button.setVisible(focused);
                        if (!focused)
                              button.setFocused(false);
                  });
            }

            hideButton.setVisible(focused);
            if (!focused) {
                  hideButton.setFocused(false);
            }
      }

      @Override
      public void setFocused(boolean b) {
            focused = b;
      }

      @Override
      public boolean isFocused() {
            return focused;
      }
      private boolean isHidden() {
            boolean menuEnabled;
            switch (Constants.CLIENT_CONFIG.menu_visibility.get()) {
                  case HIDE_ABLE -> menuEnabled = anyUnlockedNonHidden(hiddenTabs);
                  case DISABLE -> menuEnabled = false;
                  default -> menuEnabled = true;
            }

            return !menuEnabled;
      }


      HashSet<Tabs> hiddenTabs = new HashSet<>();
      private boolean anyUnlockedNonHidden(HashSet<Tabs> hiddenTabs) {
            for (Tabs value : Tabs.values()) {
                  if (!hiddenTabs.contains(value) && value.isUnlocked(minecraft))
                        return true;
            }
            return false;
      }

      private void toggleHidden() {
            boolean hidden = anyUnlockedNonHidden(Constants.CLIENT_CONFIG.hidden_tabs.get());
            HashSet<Tabs> hiddenTabs = new HashSet<>();
            if (hidden) {
                  for (Tabs tab : Tabs.values())
                        if (tab.isUnlocked(minecraft))
                              hiddenTabs.add(tab);
            }

            ClientConfig config = Constants.CLIENT_CONFIG;
            config.hidden_tabs.set(hiddenTabs);
            config.write();
      }

      public static class InfoButton extends ImageButton {
            private final Tabs tab;

            public InfoButton(Tabs tab, int leftPos, int topPos, OnPress o) {
                  super(leftPos + tab.offsetXY[0], topPos, 24, 25, 24 * tab.index, 0, 25, TEXTURE, o);
                  this.tab = tab;
                  init();
            }

            public void init() {
                  setVisible(false);
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

            public HomeButton(InfoWidget parent, OnPress o) {
                  super(Tabs.BACKPACK, parent.leftPos, parent.topPos, o);
                  this.parent = parent;
                  setVisible(!parent.isHidden());
            }

            @Override
            public void init() {

            }

            @Override
            public void setVisible(boolean visible) {
                  if (!visible && parent.isFocused())
                        parent.toggleFocus();

                  super.setVisible(visible);
            }

            @Override
            public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
                  if (!parent.focused)
                        this.renderTexture($$0, this.resourceLocation, this.getX(), this.getY(), 0, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                  else
                        super.renderWidget($$0, $$1, $$2, $$3);

            }
      }

      public static class HideButton extends Button {
            private final InfoWidget parent;

            public HideButton(InfoWidget parent, OnPress onPress) {
                  super(parent.leftPos + 44, parent.topPos + 146, 15, 16, CommonComponents.EMPTY, onPress, Supplier::get);
                  this.parent = parent;
                  setVisible(false);
                  setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable("help.beansbackpacks.hide")));
            }

            public void setVisible(boolean visible) {
                  boolean equals = Constants.CLIENT_CONFIG.menu_visibility.get().equals(MenuVisibility.HIDE_ABLE);
                  this.visible = visible && equals;
            }

            @Override
            protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
                  if (!parent.anyUnlockedNonHidden(Constants.CLIENT_CONFIG.hidden_tabs.get()))
                        this.renderTexture($$0, TEXTURE, this.getX(), this.getY(), 16, 198, getHeight(), getWidth(), getHeight(), 256, 256);
                  else
                        this.renderTexture($$0, TEXTURE, this.getX(), this.getY(), 0, 198, getHeight(), getWidth(), getHeight(), 256, 256);
            }
      }
}
