package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Iterator;

import static com.beansgalaxy.backpacks.data.config.Config.*;

@Config(name = Constants.MOD_ID)
public class FabricConfig implements ConfigData {
      @ConfigEntry.Gui.Excluded
      @ConfigEntry.Gui.CollapsibleObject
      MaximumStacks maximumStacks = new MaximumStacks();

      @ConfigEntry.Gui.CollapsibleObject
      ClientConfig clientConfig = new ClientConfig();

      @ConfigEntry.Gui.CollapsibleObject @Comment("Players may still change these values on a per-world basis")
      Gamerules gamerules = new Gamerules();

      public static int getIntConfig(com.beansgalaxy.backpacks.data.config.Config config) {
            FabricConfig instance = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
            int value = config.get(Integer.class);
            switch (config) {
                  case LEATHER_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.leather, 1, MAX_STACKS_RANGE);
                  case ENDER_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.ender, 1, MAX_ENDER_RANGE);
                  case WINGED_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.winged, 1, MAX_STACKS_RANGE);
                  case METAL_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.metal, 1, MAX_STACKS_RANGE);
                  case POT_MAX_STACKS -> value =
                              Mth.clamp(instance.maximumStacks.pot, 1, MAX_SPECIAL_RANGE);
                  case CAULDRON_MAX_BUCKETS -> value =
                              Mth.clamp(instance.maximumStacks.cauldron, 1, MAX_SPECIAL_RANGE);
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

      public static TooltipType getTooltipType() {
            FabricConfig instance = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
            return instance.clientConfig.tooltipType;
      }

      static class Gamerules {
            @Comment("Lock placed Backpacks if a player did not place it")
            boolean lockBackpackNotOwner = LOCK_BACKPACK_NOT_OWNER.get(Boolean.class);
            @Comment("Lock placed Backpacks while their owner is offline")
            boolean lockBackpackOffline = LOCK_BACKPACK_OFFLINE.get(Boolean.class);
            @Comment("Lock Ender Backpacks while their owner is offline")
            boolean lockEnderOffline = LOCK_ENDER_OFFLINE.get(Boolean.class);
            @Comment("Unbind an equipped Ender Backpack when the player dies")
            boolean unbindEnderOnDeath = UNBIND_ENDER_ON_DEATH.get(Boolean.class);
            @Comment("Does the player keep their Back Slot on death")
            boolean keepBackSlot = KEEP_BACK_SLOT.get(Boolean.class);
      }

      static class MaximumStacks {
            @Comment("Range: 1 - " + MAX_STACKS_RANGE)
            int leather = LEATHER_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - " + MAX_STACKS_RANGE)
            int winged = WINGED_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - " + MAX_STACKS_RANGE)
            int metal = METAL_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - " + MAX_ENDER_RANGE)
            int ender = ENDER_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - " + MAX_SPECIAL_RANGE)
            int pot = POT_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - " + MAX_SPECIAL_RANGE)
            int cauldron = CAULDRON_MAX_BUCKETS.get(Integer.class);
      }

      static class ClientConfig {
            @Comment("HIDDEN / HIDE_ABLE / SHOWN")
            MenuVisibility menuVisibility = MENU_VISIBILITY.get(MenuVisibility.class);
            String hiddenHelpTabs = "";
            boolean instantPlace = INSTANT_PLACE.get(Boolean.class);
            @Comment("VANILLA / COMPACT / INTEGRATED")
            TooltipType tooltipType = TooltipType.COMPACT;
      }

}
