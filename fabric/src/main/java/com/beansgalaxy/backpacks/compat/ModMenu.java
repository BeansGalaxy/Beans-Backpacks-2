package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;

import java.util.HashSet;
import java.util.Iterator;

public class ModMenu implements ModMenuApi {

      @Override
      public ConfigScreenFactory<?> getModConfigScreenFactory() {
            if (Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return screen -> AutoConfig.getConfigScreen(FabricConfig.class, screen).get();
            return screen -> null;
      }

      public static void registerConfig() {
      }

      public static int getIntConfig(com.beansgalaxy.backpacks.data.config.Config config) {
            FabricConfig instance = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();

            switch (config) {
                  case LEATHER_MAX_STACKS -> {
                        return instance.maximumStacks.leather;
                  }
                  case ENDER_MAX_STACKS -> {
                        return instance.maximumStacks.ender;
                  }
                  case WINGED_MAX_STACKS -> {
                        return instance.maximumStacks.winged;
                  }
                  case METAL_MAX_STACKS -> {
                        return instance.maximumStacks.metal;
                  }
                  case POT_MAX_STACKS -> {
                        return instance.maximumStacks.pot;
                  }
                  case CAULDRON_MAX_BUCKETS -> {
                        return instance.maximumStacks.cauldron;
                  }
            }

            return config.get(Integer.class);
      }

      public static boolean getBoolConfig(com.beansgalaxy.backpacks.data.config.Config config) {
            FabricConfig instance = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();

            switch (config) {
                  case UNBIND_ENDER_ON_DEATH -> {
                        return instance.gamerules.unbindEnderOnDeath;
                  }
                  case LOCK_ENDER_OFFLINE -> {
                        return instance.gamerules.lockEnderOffline;
                  }
            }

            return config.get(Boolean.class);
      }

      public static MenuVisibility getMenuVisibility() {
            FabricConfig instance = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
            return instance.clientConfig.menuVisibility;
      }

      public static HashSet<InfoWidget.Tab> getHiddenTabs() {
            ConfigHolder<FabricConfig> holder = AutoConfig.getConfigHolder(FabricConfig.class);
            holder.load();
            FabricConfig instance = holder.getConfig();
            String hiddenHelpTabs = instance.clientConfig.HiddenHelpTabs;

            String[] split = hiddenHelpTabs.split(",");
            HashSet<InfoWidget.Tab> tabs = new HashSet<>();
            for (String string : split) {
                  for (InfoWidget.Tab tab : InfoWidget.Tab.values()) {
                        String name = tab.name();
                        if (name.equals(string))
                              tabs.add(tab);
                  }
            }

            return tabs;
      }

      public static void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs) {
            StringBuilder sb = new StringBuilder();
            Iterator<InfoWidget.Tab> iterator = hiddenTabs.iterator();
            while (iterator.hasNext()) {
                  InfoWidget.Tab tab = iterator.next();
                  sb.append(tab.name());
                  if (iterator.hasNext()) sb.append(",");
            }

            ConfigHolder<FabricConfig> holder = AutoConfig.getConfigHolder(FabricConfig.class);
            FabricConfig instance = holder.get();
            instance.clientConfig.HiddenHelpTabs = sb.toString();
            holder.save();
      }

}
