package com.beansgalaxy.backpacks.config.screen;

import com.beansgalaxy.backpacks.config.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientConfigRows extends ConfigRows {
      public ClientConfigRows(ConfigScreen screen, Minecraft minecraft) {
            super(screen, minecraft);
      }

      @Override
      public List<ConfigLabel> getRows() {
            return List.of(
                        new ConfigLabel(Component.literal("CLIENT1")),
                        new ConfigLabel(Component.literal("CLIENT2"))
            );
      }
}
