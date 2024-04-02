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
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
import java.util.function.Function;

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
      private Tab selected = Tab.BACKPACK;
      public NonNullList<Optional<InfoButton>> buttons = NonNullList.create();
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
            for (Optional<InfoButton> optional : buttons) {
                  optional.ifPresent(button -> {
                        Tab tab = button.tab;
                        button.setPosition(leftPos + tab.offsetXY[0], topPos);
                  });
            }
      }

      @Override
      public void render(GuiGraphics ctx, int i, int i1, float v) {
            if (focused) {
                  ctx.blit(TEXTURE, leftPos - 166, topPos + 22, 0, 1, 53, 167, 144, 256, 256);
                  Component textVariable = selected.getTextVariable(minecraft);
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

                  String useKey = "§0" + minecraft.options.keyUse.getTranslatedKeyMessage().getString()
                              .replace("Right Button", "RClick")
                              .replace("Left Button", "LClick")
                              .replace("Left ", "L")
                              .replace("Right ", "R");

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
                              MutableComponent mutableComponent = Component.translatableWithFallback(key, "", textVariable, useKey);
                              visualOrderText = mutableComponent.getVisualOrderText();
                              drawCenteredText(ctx, visualOrderText, this.topPos + stringY - 1);
                              hLineY = 0;
                              stringY += 9;
                        }
                  }

                  for (Tab tab : Tab.values()) {
                        if (tab.tabUnlocked.apply(minecraft)) {
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

      public void init(int height, int leftPos, int topPos, Minecraft minecraft, RecipeBookComponent recipeBookComponent, Runnable onClick) {
            this.minecraft = minecraft;
            this.height = height;
            this.leftPos = leftPos;
            this.topPos = topPos + 1;
            this.onClick = onClick;
            this.recipeBook = recipeBookComponent;
            focused = false;
            this.homeButton = new HomeButton(this.leftPos, this.topPos, this, (button) -> {
                  onButtonClick(Tab.BACKPACK);
            });

            updateVisible();

            buttons.clear();
            buttons.add(Optional.of(homeButton));
            for (Tab tab : Tab.values()) {
                  if (tab == Tab.BACKPACK) continue;
                  buttons.add(createInfoButton(tab));
            }
      }

      public void updateVisible() {
            boolean RecipeBookClosed = !recipeBook.isVisible();
            Boolean BeganProgress = Tab.BACKPACK.tabUnlocked.apply(minecraft);
            homeButton.setVisible(RecipeBookClosed && BeganProgress);
      }

      private Optional<InfoButton> createInfoButton(Tab tab) {
            if (tab.isUnlocked(minecraft)) {
                  return Optional.of(new InfoButton(tab, this.leftPos, this.topPos, (button) -> {
                        onButtonClick(tab);
                  }));
            }
            else return Optional.empty();
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
            for (Optional<InfoButton> optional : buttons) {
                  optional.ifPresent(button -> {
                        if (button != homeButton)
                              button.setVisible(focused);
                        if (!focused)
                              button.setFocused(false);
                  });
            }
      }

      public static String keyBind = "§0" + Tooltip.getKeyBinding().getTranslatedKeyMessage().getString()
                  .replace("Left ", "L")
                  .replace("Right ", "R")
                  .replace("Control", "Ctrl");

      public enum Tab {
            BACKPACK
                        (1, new int[]{-23, 0},
                        Services.REGISTRY.getLeather().getDefaultInstance(),
                        in -> getAdvancement(in.getConnection(), "beansbackpacks:beansbackpacks"),
                        in -> Component.literal(InfoWidget.keyBind),
                        0x19EE5500
            ),
            ENDER       (2, new int[]{-46, 0},
                        Services.REGISTRY.getEnder().getDefaultInstance(),
                        in -> getAdvancement(in.getConnection(), "beansbackpacks:info/ender_backpacks"),
                        in -> Component.literal(InfoWidget.keyBind),
                        0x106600FF
            ),
            POT         (3, new int[]{-69, 0},
                        Items.DECORATED_POT.getDefaultInstance(),
                        in -> getAdvancement(in.getConnection(), "beansbackpacks:info/decorated_pots"),
                        in -> Component.literal(InfoWidget.keyBind),
                        0x19DDCC00
            ),
            CAULDRON    (4, new int[]{-92, -1},
                        Items.CAULDRON.getDefaultInstance(),
                        in -> getAdvancement(in.getConnection(), "beansbackpacks:info/fluid_cauldrons"),
                        in -> Component.literal(InfoWidget.keyBind),
                        0x120055FF
            ),
            NULL        (5, new int[]{-114, 0},
                        Constants.createLabeledBackpack("null"),
                        in -> getAdvancement(in.getConnection(), "beansbackpacks:info/thank_you"),
                        in -> in.player.getName().plainCopy().withStyle(ChatFormatting.BLACK),
                        0x1000FF00
            );

            final int index;
            final Function<Minecraft, Boolean> tabUnlocked;
            final int[] offsetXY;
            final ItemStack display;
            final Function<Minecraft, Component> textVariable;
            final Color color;

            Tab(int i, int[] offsetXY, ItemStack display, Function<Minecraft, Boolean> tabUnlocked, Function<Minecraft, Component> textVariable, int color) {
                  index = i;
                  this.tabUnlocked = tabUnlocked;
                  this.offsetXY = offsetXY;
                  this.display = display;
                  this.textVariable = textVariable;
                  this.color = new Color(color, true);
            }

            private boolean isUnlocked(Minecraft minecraft) {
                  return tabUnlocked.apply(minecraft);
            }
            public static boolean getAdvancement(ClientPacketListener connection, String location) {
                  if (connection == null)
                        return false;

                  ResourceLocation resourceLocation = ResourceLocation.tryParse(location);
                  if (resourceLocation == null) {
                        return false;
                  }
                  return connection.getAdvancements().getAdvancements().get(resourceLocation) != null;
            }

            public void render(GuiGraphics gui, int x, int y) {
                  gui.renderFakeItem(display, x + offsetXY[0] - offsetXY[1] + 23, y + offsetXY[1]);
            }

            public Component getTextVariable(Minecraft minecraft) {
                  return textVariable.apply(minecraft);
            }
      }

      public static class InfoButton extends ImageButton {
            private final Tab tab;

            public InfoButton(Tab tab, int leftPos, int topPos, OnPress o) {
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

            public HomeButton(int leftPos, int topPos, InfoWidget parent, OnPress o) {
                  super(Tab.BACKPACK, leftPos, topPos, o);
                  this.parent = parent;
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
}
