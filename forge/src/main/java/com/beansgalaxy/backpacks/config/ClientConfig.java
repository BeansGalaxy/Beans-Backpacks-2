package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ClientConfig {
      public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
      public static final ForgeConfigSpec SPEC;

      public static final ForgeConfigSpec.EnumValue<MenuVisibility> MENU_VISIBILITY;
      public static final ForgeConfigSpec.ConfigValue<List<? extends InfoWidget.Tab>> HIDDEN_TABS;

      static {
            BUILDER.push("Beans Backpacks Config");

            MENU_VISIBILITY = BUILDER.defineEnum("Inventory Help Menu Visibility",
                        Config.MENU_VISIBILITY.get(MenuVisibility.class));

            List<InfoWidget.Tab> tabs = List.of();
            HIDDEN_TABS = BUILDER.defineList("Hidden Help Menu Tabs", tabs, o -> o instanceof InfoWidget.Tab);

            BUILDER.pop();
            SPEC = BUILDER.build();
      }
}
