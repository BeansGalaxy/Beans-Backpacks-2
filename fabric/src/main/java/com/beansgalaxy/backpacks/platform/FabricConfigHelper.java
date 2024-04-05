package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.FabricConfig;
import com.beansgalaxy.backpacks.compat.ModMenu;
import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.InfoWidget;

import java.util.HashSet;

public class FabricConfigHelper implements ConfigHelper {
      @Override
      public int getIntConfig(Config config) {
            if (!Services.COMPAT.allModsLoaded(CompatHelper.MOD_MENU, CompatHelper.CLOTH_CONFIG))
                  return config.get(Integer.class);
            return ModMenu.getIntConfig(config);
      }

      @Override
      public boolean getBoolConfig(Config config) {
            if (!Services.COMPAT.allModsLoaded(CompatHelper.MOD_MENU, CompatHelper.CLOTH_CONFIG))
                  return config.get(Boolean.class);
            return ModMenu.getBoolConfig(config);
      }

      @Override
      public MenuVisibility getMenuVisibility() {
            if (!Services.COMPAT.allModsLoaded(CompatHelper.MOD_MENU, CompatHelper.CLOTH_CONFIG))
                  return Config.MENU_VISIBILITY.get(MenuVisibility.class);
            return ModMenu.getMenuVisibility();
      }

      @Override
      public HashSet<InfoWidget.Tab> getHiddenTabs() {
            if (!Services.COMPAT.allModsLoaded(CompatHelper.MOD_MENU, CompatHelper.CLOTH_CONFIG))
                  return new HashSet<>();
            return ModMenu.getHiddenTabs();
      }

      @Override
      public void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs) {
            if (Services.COMPAT.allModsLoaded(CompatHelper.MOD_MENU, CompatHelper.CLOTH_CONFIG))
                  ModMenu.saveHiddenTabs(hiddenTabs);
      }
}
