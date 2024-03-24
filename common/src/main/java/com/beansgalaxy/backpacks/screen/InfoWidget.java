package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class InfoWidget implements Renderable, GuiEventListener, NarratableEntry {
      private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/info_tab.png");
      private static final int[] INFO_TEXT_UV = {85, 137};
      private Minecraft minecraft;
      private int width;
      private int height;
      private int leftPos;
      private int topPos;
      private InventoryMenu menu;
      private boolean focused = false;
      private Runnable onClick;
      private Tab selected = Tab.HOME;
      public NonNullList<InfoButton> buttons = NonNullList.create();
      public HomeButton homeButton;

      public enum Tab {
            HOME,
            BACKPACK,
            ENDER,
            POT,
            CAULDRON
      }

      @Override
      public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
            if (focused) {
                  guiGraphics.blit(TEXTURE, leftPos - 85, topPos + 26, 0, 1, 53, 85, 137, 256, 256);
                  guiGraphics.drawString(minecraft.font, Component.literal(selected.name().toLowerCase()), this.leftPos - 60, this.topPos + 50, 4210752, false);
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

      @Override @NotNull
      public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
      }

      @Override
      public void updateNarration(NarrationElementOutput narrationElementOutput) {

      }

      public void init(int width, int height, int leftPos, int topPos, Minecraft minecraft, InventoryMenu menu, Runnable onClick) {
            this.minecraft = minecraft;
            this.width = width;
            this.height = height;
            this.leftPos = leftPos;
            this.topPos = topPos;
            this.menu = menu;
            this.onClick = onClick;
            focused = false;
            buttons.clear();
            this.homeButton = new HomeButton(this.leftPos, this.topPos, (button) -> {
                  onButtonClick(Tab.HOME);
            });
            buttons.add(new InfoButton(1, this.leftPos, this.topPos, (button) -> {
                  onButtonClick(Tab.BACKPACK);
            }));
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
            if (selected == Tab.HOME) {
                  setSelected(Tab.BACKPACK);
                  this.toggleFocus();
            }
            else if (this.selected == selected) {
                  setSelected(Tab.HOME);
                  this.toggleFocus();
            }
            else setSelected(selected);
            onClick.run();
      }

      public void setSelected(Tab selected) {
            this.selected = selected;
      }

      public void toggleFocus() {
            setFocused(!focused);
            for (InfoButton button : buttons) {
                  button.setVisible(focused);
                  if (!focused)
                        button.setFocused(false);
            }
            homeButton.setVisible(!focused);
            homeButton.setFocused(focused);
      }

      public static class InfoButton extends ImageButton {
            public InfoButton(int index, int leftPos, int topPos, OnPress o) {
                  super(leftPos - (21 * index), topPos + 4, 21, 25, 24 * index, 0, 25, TEXTURE, o);
                  visible = false;
            }

            public void setVisible(boolean hidden) {
                  this.visible = hidden;
            }

            @Override
            public void setFocused(boolean focused) {
                  super.setFocused(focused);
            }
      }

      public static class HomeButton extends ImageButton {
            public HomeButton(int leftPos, int topPos, OnPress o) {
                  super(leftPos - 21, topPos + 4, 21, 24, 0, 0, 25, TEXTURE, o);
            }

            public void setVisible(boolean hidden) {
                  this.visible = hidden;
            }

            @Override
            public void setFocused(boolean focused) {
                  super.setFocused(focused);
            }
      }
}
