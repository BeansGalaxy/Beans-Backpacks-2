package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.screen.ClientConfigRows;
import com.beansgalaxy.backpacks.config.screen.CommonConfigRows;
import com.beansgalaxy.backpacks.config.screen.ConfigRows;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfigScreen extends Screen {
      private final Screen lastScreen;
      private ClientConfigRows clientConfigRows;
      private CommonConfigRows commonConfigRows;
      private ConfigRows currentPage;

      public ConfigScreen(Screen lastScreen) {
            super(Component.empty());
            this.lastScreen = lastScreen;
      }

      @Override
      protected void init() {
            super.init();
            clientConfigRows = new ClientConfigRows(this, minecraft);
            commonConfigRows = new CommonConfigRows(this, minecraft);
            currentPage = commonConfigRows;
            addWidgets();
      }

      private void addWidgets() {
            int center = this.width / 2;
            this.addRenderableWidget(Button.builder(Component.literal("Reset"), ($$0) -> {
                  // RESET CONFIG
            }).bounds(center - 165, this.height - 26, 70, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Cancel"), ($$0) -> {
                  // CANCEL CONFIG
            }).bounds(center - 80, this.height - 26, 70, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Save Changes"), ($$0) -> {
                  this.minecraft.setScreen(this.lastScreen);
            }).bounds(center + 5, this.height - 26, 160, 20).build());

            MutableComponent client = Component.literal("Client Config");
            int clientWidth = minecraft.font.width(client);
            this.addRenderableWidget(new PlainTextButton(center + 20, 20, clientWidth, 20, client, in -> {
                  currentPage = clientConfigRows;
            }, minecraft.font){
                  @Override
                  public boolean isFocused() {
                        return currentPage == clientConfigRows;
                  }
            });

            MutableComponent common = Component.literal("Common Config");
            int commonWidth = minecraft.font.width(common);
            this.addRenderableWidget(new PlainTextButton(center - commonWidth - 20, 20, commonWidth, 20, common, in -> {
                  currentPage = commonConfigRows;
            }, minecraft.font) {
                  @Override
                  public boolean isFocused() {
                        return currentPage == commonConfigRows;
                  }
            });
      }

      @Override
      public void onClose() {
            this.minecraft.setScreen(this.lastScreen);
      }

      @Override
      public void render(GuiGraphics gui, int x, int y, float delta) {
            renderBackground(gui);
            gui.drawCenteredString(minecraft.font, Component.literal(Constants.MOD_NAME).withStyle(ChatFormatting.BOLD), minecraft.getWindow().getGuiScaledWidth() / 2, 6, 0xFFCCDDFF);
            currentPage.render(gui, x, y, delta);
            super.render(gui, x, y, delta);
      }

      @Override
      public void renderBackground(GuiGraphics gui) {
            super.renderBackground(gui);
      }



}
