package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

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

      static class Gamerules {
            @Comment("Unbind an equipped Ender Backpack when the player dies")
            boolean unbindEnderOnDeath = UNBIND_ENDER_ON_DEATH.get(Boolean.class);
            @Comment("Lock Ender Backpacks while their owner is offline")
            boolean lockEnderOffline = LOCK_ENDER_OFFLINE.get(Boolean.class);
      }

      static class MaximumStacks {
            @Comment("Range: 1 - 64")
            int leather = LEATHER_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - 64")
            int winged = WINGED_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - 64")
            int metal = METAL_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - 8")
            int ender = ENDER_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - 128")
            int pot = POT_MAX_STACKS.get(Integer.class);
            @Comment("Range: 1 - 128")
            int cauldron = CAULDRON_MAX_BUCKETS.get(Integer.class);
      }

      static class ClientConfig {
            @Comment("HIDDEN / HIDE_ABLE / SHOWN")
            MenuVisibility menuVisibility = MENU_VISIBILITY.get(MenuVisibility.class);
            String hiddenHelpTabs = "";
            boolean instantPlace = INSTANT_PLACE.get(Boolean.class);
      }

}
