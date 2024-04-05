package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
      public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
      public static final ForgeConfigSpec SPEC;

      public static final ForgeConfigSpec.IntValue LEATHER_MAX_STACKS;
      public static final ForgeConfigSpec.IntValue ENDER_MAX_STACKS;
      public static final ForgeConfigSpec.IntValue WINGED_MAX_STACKS;
      public static final ForgeConfigSpec.IntValue METAL_MAX_STACKS;
      public static final ForgeConfigSpec.IntValue POT_MAX_STACKS;
      public static final ForgeConfigSpec.IntValue CAULDRON_MAX_STACKS;
      public static final ForgeConfigSpec.BooleanValue UNBIND_ENDER_ON_DEATH;
      public static final ForgeConfigSpec.BooleanValue LOCK_ENDER_OFFLINE;


      static {
            BUILDER.push("Configure Backpack Sizes");
            int minStacks = Config.MAX_STACKS_RANGE[0];
            int maxStacks = Config.MAX_STACKS_RANGE[1];

            LEATHER_MAX_STACKS =    BUILDER.defineInRange("Leather Backpacks Maximum Stacks",
                        Config.LEATHER_MAX_STACKS.get(Integer.class), minStacks, maxStacks);
            ENDER_MAX_STACKS =      BUILDER.defineInRange("Ender Backpacks Maximum Stacks",
                        Config.ENDER_MAX_STACKS.get(Integer.class), minStacks, 8);
            WINGED_MAX_STACKS =     BUILDER.defineInRange("Winged Backpacks Maximum Stacks",
                        Config.WINGED_MAX_STACKS.get(Integer.class), minStacks, maxStacks);
            METAL_MAX_STACKS =      BUILDER.defineInRange("Iron & Undefined Metal Backpacks Maximum Stacks",
                        Config.METAL_MAX_STACKS.get(Integer.class), minStacks, maxStacks);

            POT_MAX_STACKS =      BUILDER.defineInRange("Equipped Decorated Pots Maximum Stacks",
                        Config.POT_MAX_STACKS.get(Integer.class), minStacks, 128);
            CAULDRON_MAX_STACKS =   BUILDER.defineInRange("Equipped Cauldrons Maximum Buckets",
                        Config.CAULDRON_MAX_BUCKETS.get(Integer.class), minStacks, 128);

            BUILDER.pop();
            BUILDER.push("Configure Gamerule Defaults").comment("Players may still change these values on a per-world basis", "");

            UNBIND_ENDER_ON_DEATH = BUILDER.comment("Unbind an equipped Ender Backpack when a player dies")
                        .define("unbindEnderOnDeath", Config.UNBIND_ENDER_ON_DEATH.get(Boolean.class).booleanValue());
            LOCK_ENDER_OFFLINE = BUILDER.comment("Lock Ender Backpacks while their owner is offline")
                        .define("lockEnderWhenLoggedOff", Config.UNBIND_ENDER_ON_DEATH.get(Boolean.class).booleanValue());

            BUILDER.pop();

            SPEC = BUILDER.build();
      }
}
