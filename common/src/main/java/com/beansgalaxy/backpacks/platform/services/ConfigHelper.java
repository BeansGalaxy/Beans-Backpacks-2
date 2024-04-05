package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.screen.InfoWidget;

import java.util.HashSet;

public interface ConfigHelper {
      int getIntConfig(Config config);

      boolean getBoolConfig(Config config);

      MenuVisibility getMenuVisibility();

      HashSet<InfoWidget.Tab> getHiddenTabs();

      void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs);
}
