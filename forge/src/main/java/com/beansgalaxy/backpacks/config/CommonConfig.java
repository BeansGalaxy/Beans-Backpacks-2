package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.data.config.Config;
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
      public static final ForgeConfigSpec.BooleanValue LOCK_BACKPACK_OFFLINE;
      public static final ForgeConfigSpec.BooleanValue LOCK_BACKPACK_NOT_OWNER;
      public static final ForgeConfigSpec.BooleanValue LOCK_BACKPACK_NO_KEY;
      public static final ForgeConfigSpec.BooleanValue KEEP_BACK_SLOT;


      static {
            BUILDER.push("Configure Backpack Sizes");

            LEATHER_MAX_STACKS =    BUILDER.defineInRange("Leather Backpacks Maximum Stacks",
                        Config.LEATHER_MAX_STACKS.get(Integer.class), 1, Config.MAX_STACKS_RANGE);
            ENDER_MAX_STACKS =      BUILDER.defineInRange("Ender Backpacks Maximum Stacks",
                        Config.ENDER_MAX_STACKS.get(Integer.class), 1, Config.MAX_ENDER_RANGE);
            WINGED_MAX_STACKS =     BUILDER.defineInRange("Winged Backpacks Maximum Stacks",
                        Config.WINGED_MAX_STACKS.get(Integer.class), 1, Config.MAX_STACKS_RANGE);
            METAL_MAX_STACKS =      BUILDER.defineInRange("Iron & Undefined Metal Backpacks Maximum Stacks",
                        Config.METAL_MAX_STACKS.get(Integer.class), 1, Config.MAX_STACKS_RANGE);

            POT_MAX_STACKS =        BUILDER.defineInRange("Equipped Decorated Pots Maximum Stacks",
                        Config.POT_MAX_STACKS.get(Integer.class), 1, Config.MAX_SPECIAL_RANGE);
            CAULDRON_MAX_STACKS =   BUILDER.defineInRange("Equipped Cauldrons Maximum Buckets",
                        Config.CAULDRON_MAX_BUCKETS.get(Integer.class), 1, Config.MAX_SPECIAL_RANGE);

            BUILDER.pop();
            BUILDER.push("Configure Gamerule Defaults").comment("Players may still change these values on a per-world basis", "");

            LOCK_BACKPACK_NOT_OWNER =    BUILDER.comment("Lock placed Backpacks if a player did not place it")
                        .define("lockBackpackNotOwner", Config.LOCK_BACKPACK_NOT_OWNER.get(Boolean.class).booleanValue());
            LOCK_BACKPACK_OFFLINE =    BUILDER.comment("Lock placed Backpacks while their owner is offline")
                        .define("lockBackpackOffline", Config.LOCK_BACKPACK_OFFLINE.get(Boolean.class).booleanValue());
            LOCK_ENDER_OFFLINE =    BUILDER.comment("Lock Ender Backpacks while their owner is offline")
                        .define("lockEnderWhenLoggedOff", Config.LOCK_ENDER_OFFLINE.get(Boolean.class).booleanValue());
            LOCK_BACKPACK_NO_KEY =    BUILDER.comment("Equipped backpacks are locked to other players if hotkey isn't pressed")
                        .define("lockBackpackNoKey", Config.LOCK_BACKPACK_NO_KEY.get(Boolean.class).booleanValue());

            UNBIND_ENDER_ON_DEATH = BUILDER.comment("Unbind an equipped Ender Backpack when a player dies")
                        .define("unbindEnderOnDeath", Config.UNBIND_ENDER_ON_DEATH.get(Boolean.class).booleanValue());
            KEEP_BACK_SLOT =        BUILDER.comment("Does the player keep their Back Slot on death")
                        .define("keepBackSlot", Config.KEEP_BACK_SLOT.get(Boolean.class).booleanValue());

            BUILDER.pop();

            SPEC = BUILDER.build();
      }
}
