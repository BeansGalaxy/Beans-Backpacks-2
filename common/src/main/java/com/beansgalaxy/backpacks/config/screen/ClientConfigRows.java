package com.beansgalaxy.backpacks.config.screen;

import com.beansgalaxy.backpacks.config.ClientConfig;
import com.beansgalaxy.backpacks.config.ConfigScreen;
import com.beansgalaxy.backpacks.data.config.BackpackCapePos;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;
import java.util.Optional;

public class ClientConfigRows extends ConfigRows {
      public ClientConfigRows(ConfigScreen screen, Minecraft minecraft, ClientConfig config) {
            super(screen, minecraft, config);
      }

      @Override
      public List<ConfigLabel> getRows() {
            return List.of(
                        new BoolConfigRow(screen.clientConfig.instant_place),
                        new EnumConfigRow<>(screen.clientConfig.backpack_cape_pos, BackpackCapePos.values()),
                        new EnumConfigRow<>(screen.clientConfig.menu_visibility, MenuVisibility.values()),
                        new EnumConfigRow<>(screen.clientConfig.tooltip_style, TooltipType.values())
            );
      }

      @Override
      public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
            return super.getChildAt($$0, $$1);
      }
}
