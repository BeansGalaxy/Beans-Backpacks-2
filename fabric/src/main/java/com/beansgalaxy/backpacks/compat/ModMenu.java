package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.util.Mth;

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
            int value = config.get(Integer.class);
            switch (config) {
                  case LEATHER_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.leather, 1, Config.MAX_STACKS_RANGE);
                  case ENDER_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.ender, 1, Config.MAX_ENDER_RANGE);
                  case WINGED_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.winged, 1, Config.MAX_STACKS_RANGE);
                  case METAL_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.metal, 1, Config.MAX_STACKS_RANGE);
                  case POT_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.pot, 1, Config.MAX_SPECIAL_RANGE);
                  case CAULDRON_MAX_BUCKETS -> value =
                              Mth.clamp(instance.maximumStacks.cauldron, 1, Config.MAX_SPECIAL_RANGE);
            }
            return value;
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
                  case LOCK_BACKPACK_OFFLINE -> {
                        return instance.gamerules.lockBackpackOffline;
                  }
                  case LOCK_BACKPACK_NOT_OWNER -> {
                        return instance.gamerules.lockBackpackNotOwner;
                  }
                  case KEEP_BACK_SLOT -> {
                        return instance.gamerules.keepBackSlot;
                  }
                  case INSTANT_PLACE -> {
                        return instance.clientConfig.instantPlace;
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
            String hiddenHelpTabs = instance.clientConfig.hiddenHelpTabs;

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
            instance.clientConfig.hiddenHelpTabs = sb.toString();
            holder.save();
      }

}
