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
      }

}
