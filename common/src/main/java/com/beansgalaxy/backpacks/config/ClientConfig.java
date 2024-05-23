package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.config.types.*;
import com.beansgalaxy.backpacks.data.config.BackpackCapePos;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.screen.InfoTabs.Tabs;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ClientConfig implements IConfig {
      public BoolConfigVariant instant_place;
      public BoolConfigVariant sneak_default = new BoolConfigVariant("sneak_default", false);
      public EnumConfigVariant<MenuVisibility> menu_visibility;
      public HSetConfigVariant<Tabs> hidden_tabs;
      public EnumConfigVariant<TooltipType> tooltip_style;
      public EnumConfigVariant<BackpackCapePos> backpack_cape_pos;

      private final ConfigLine[] LINES = new ConfigLine[] {
                  new ConfigLabel("Client"),
                  instant_place =         new BoolConfigVariant("instant_place", false),
                  menu_visibility =     new EnumConfigVariant<>("menu_visibility", MenuVisibility.HIDE_ABLE, MenuVisibility.values()),
                  hidden_tabs = HSetConfigVariant.Builder.create(Enum::name, Tabs::valueOf).comment("For Internal Use (Employees Only!)")
                                                         .build("hidden_tabs"),
                  tooltip_style =       new EnumConfigVariant<>("tooltip_style", TooltipType.COMPACT, TooltipType.values()),
                  backpack_cape_pos =   new EnumConfigVariant<>("backpack_cape_pos", BackpackCapePos.ON_TOP, BackpackCapePos.values())
      };

      @Override
      public String getPath() {
            return "-client";
      }

      @Override
      public Collection<ConfigLine> getLines() {
            return List.of(LINES);
      }
}
