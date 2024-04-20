package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.HashSet;

public interface ConfigHelper {
      static boolean keepBackSlot(Level level) {
            return level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || Services.CONFIG.getBoolConfig(Config.KEEP_BACK_SLOT);
      }

      int getIntConfig(Config config);

      boolean getBoolConfig(Config config);

      MenuVisibility getMenuVisibility();

      HashSet<InfoWidget.Tab> getHiddenTabs();

      void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs);

      public TooltipType getTooltipType();
}
