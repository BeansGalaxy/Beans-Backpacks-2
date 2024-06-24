package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.screen.ClientConfigRows;
import com.beansgalaxy.backpacks.config.screen.CommonConfigRows;
import com.beansgalaxy.backpacks.config.screen.ConfigRows;
import com.beansgalaxy.backpacks.data.ServerSave;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ConfigScreen extends Screen {
      private final Screen lastScreen;
      private ClientConfigRows clientConfigRows;
      private CommonConfigRows commonConfigRows;
      private ConfigRows currentPage;
      public ClientConfig clientConfig = new ClientConfig();
      public CommonConfig commonConfig = new CommonConfig();

      public ConfigScreen(Screen lastScreen) {
            super(Component.empty());
            this.lastScreen = lastScreen;
            clientConfig.read();
            commonConfig.read();
      }

      @Override
      public List<? extends GuiEventListener> children() {
            List<? extends GuiEventListener> children = super.children().stream().filter(in -> !(in != currentPage && in instanceof ConfigRows)).toList();
            return children;
      }

      @Override
      protected void init() {
            super.init();
            clientConfigRows = new ClientConfigRows(this, minecraft, clientConfig);
            commonConfigRows = new CommonConfigRows(this, minecraft, commonConfig);
            ServerSave.CONFIG.read(false);
            currentPage = commonConfigRows;
            addWidgets();
            addWidget(clientConfigRows);
            addWidget(commonConfigRows);
      }

      private void addWidgets() {
            int center = this.width / 2;
            this.addRenderableWidget(Button.builder(Component.translatable("screen.beansbackpacks.config.main.reset_all"), ($$0) -> {
                  for (ConfigRows.ConfigLabel row : currentPage.getRows())
                        row.resetToDefault();
            }).bounds(center - 165, this.height - 26, 70, 20).build());

            this.addRenderableWidget(Button.builder(Component.translatable("screen.beansbackpacks.config.main.undo_all"), ($$0) -> {
                  clientConfig.read(false);
                  commonConfig.read(false);
            }).bounds(center - 80, this.height - 26, 70, 20).build());

            this.addRenderableWidget(Button.builder(Component.translatable("screen.beansbackpacks.config.main.save_and_exit"), ($$0) -> {
                  clientConfig.write();
                  commonConfig.write();
                  this.minecraft.setScreen(this.lastScreen);
            }).bounds(center + 5, this.height - 26, 160, 20).build());

            MutableComponent client = Component.translatable("screen.beansbackpacks.config.title.client");
            int clientWidth = minecraft.font.width(client);
            this.addRenderableWidget(new PlainTextButton(center + 20, 20, clientWidth, 20, client, in -> {
                  currentPage = clientConfigRows;
            }, minecraft.font){
                  @Override
                  public boolean isFocused() {
                        return currentPage == clientConfigRows;
                  }
            });

            MutableComponent common = Component.translatable("screen.beansbackpacks.config.title.common");
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

      public void onSave() {
            for (ConfigRows.ConfigLabel row : commonConfigRows.getRows())
                  row.onSave();
            for (ConfigRows.ConfigLabel row : clientConfigRows.getRows())
                  row.onSave();
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
