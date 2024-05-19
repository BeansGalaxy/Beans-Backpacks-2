package com.beansgalaxy.backpacks.config.screen;

import com.beansgalaxy.backpacks.config.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ConfigRows extends ContainerObjectSelectionList<ConfigRows.ConfigLabel> {
      public ConfigRows(ConfigScreen screen, Minecraft minecraft) {
            super(minecraft, screen.width, screen.height - 80, 35, screen.height - 32, 25);
            //super(minecraft, width, height, y0, y1, spacing);
            for (ConfigLabel row : getRows()) {
                  addEntry(row);
            }
      }

      public abstract List<ConfigLabel> getRows();

      public class ConfigLabel extends ContainerObjectSelectionList.Entry<ConfigLabel> {
            public final Component name;

            public ConfigLabel(Component name) {
                  this.name = name;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawCenteredString(minecraft.font, name, minecraft.getWindow().getGuiScaledWidth() / 2, rowHeight * index + 45, 0xFFFFFFFF);
            }

            @Override // BUTTONS GO HERE
            public List<? extends GuiEventListener> children() {
                  return List.of();
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of();
            }
      }
}
