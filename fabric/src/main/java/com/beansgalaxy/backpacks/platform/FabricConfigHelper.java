package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.FabricConfig;
import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.InfoWidget;

import java.util.HashSet;

public class FabricConfigHelper implements ConfigHelper {
      @Override
      public int getIntConfig(Config config) {
            if (!Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return config.get(Integer.class);
            return FabricConfig.getIntConfig(config);
      }

      @Override
      public boolean getBoolConfig(Config config) {
            if (!Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return config.get(Boolean.class);
            return FabricConfig.getBoolConfig(config);
      }

      @Override
      public MenuVisibility getMenuVisibility() {
            if (!Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return Config.MENU_VISIBILITY.get(MenuVisibility.class);
            return FabricConfig.getMenuVisibility();
      }

      @Override
      public HashSet<InfoWidget.Tab> getHiddenTabs() {
            if (!Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return new HashSet<>();
            return FabricConfig.getHiddenTabs();
      }

      @Override
      public void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs) {
            if (Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  FabricConfig.saveHiddenTabs(hiddenTabs);
      }
}
