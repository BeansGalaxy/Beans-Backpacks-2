package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.config.types.*;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.screen.InfoWidget;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ClientConfig implements IConfig {
      public BoolConfigVariant instant_place;
      public EnumConfigVariant<MenuVisibility> menu_visibility;
      public HSetConfigVariant<InfoWidget.Tab> hidden_tabs;
      public EnumConfigVariant<TooltipType> tooltip_style;

      private final ConfigLine[] LINES = new ConfigLine[] {
                  new ConfigLabel("Client"),
                  instant_place =         new BoolConfigVariant("instant_place", false),
                  menu_visibility =     new EnumConfigVariant<>("menu_visibility", MenuVisibility.HIDE_ABLE, MenuVisibility.values()),
                  hidden_tabs =         new HSetConfigVariant<>("hidden_tabs", new HashSet<>(), Enum::name, InfoWidget.Tab::valueOf, "For Internal Use (Employees Only!)"),
                  tooltip_style =       new EnumConfigVariant<>("tooltip_style", TooltipType.COMPACT, TooltipType.values())
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
