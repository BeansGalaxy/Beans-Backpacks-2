package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.types.*;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import java.util.*;

public class CommonConfig implements IConfig {
      public IntConfigVariant leather_max_stacks;
      public IntConfigVariant ender_max_stacks;
      public IntConfigVariant winged_max_stacks;
      public IntConfigVariant metal_max_stacks;
      public IntConfigVariant pot_max_stacks;
      public IntConfigVariant cauldron_max_buckets;
      public HSetConfigVariant<Item> blacklist_items;
      public HSetConfigVariant<Item> disable_chestplate;
      public HSetConfigVariant<Item> disables_back_slot;
      public HSetConfigVariant<Item> elytra_items;
      public HMapConfigVariant<String, Integer> data_driven_overrides;
      public BoolConfigVariant always_disables_back_slot;

      public boolean usesOldDataPackConfig = false;

      private final ConfigLine[] LINES = new ConfigLine[] {
                  new ConfigLabel("Maximum Stacks"),
                  leather_max_stacks =       new IntConfigVariant("leather_max_stacks", 4, 1, 64),
                  ender_max_stacks =         new IntConfigVariant("ender_max_stacks",   4, 1, 8),
                  winged_max_stacks =        new IntConfigVariant("winged_max_stacks",  4, 1, 64),
                  metal_max_stacks =         new IntConfigVariant("metal_max_stacks",   7, 1, 64),
                  pot_max_stacks =           new IntConfigVariant("pot_max_stacks",   128, 0, 128),
                  cauldron_max_buckets =  new IntConfigVariant("cauldron_max_buckets", 24, 0, 128),
                  data_driven_overrides = HMapConfigVariant.Builder.create(String::valueOf, Integer::valueOf)
                              .example(new String[]{"gold", "netherite"}, new Integer[]{7, 11}).validator(i -> Mth.clamp(i, 1, 64))
                              .comment("If a backpack is Data-Driven, find it's 'backpack_id' with F3 + H")
                              .build("data_driven_overrides"),
                  new ConfigLabel("Item Whitelists"),
                  new ConfigComment("┏▶ items can be worn on the back & not as equipment. Item does not keep functioning or rendering on the back."),
                  disable_chestplate = ItemListConfigVariant.create("disable_chestplate"),
                  new ConfigComment("┏▶ if any items are in armor/trinkets/curios slots then back equipment is not rendered."),
                  disables_back_slot = ItemListConfigVariant.create("disables_back_slot", "create:copper_backtank", "create:netherite_backtank"),
                  new ConfigComment("┏▶ cannot be worn with Winged Backpack & other backpacks will be positioned off the player's back"),
                  elytra_items       = ItemListConfigVariant.create("elytra_items", "minecraft:elytra"),
                  new ConfigComment("┏▶ cannot be stored in any backpack's inventory"),
                  blacklist_items    = ItemListConfigVariant.create("blacklist_items", "minecraft:shulker_box", "minecraft:white_shulker_box",
                              "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box",
                              "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box",
                              "minecraft:gray_shulker_box", "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box",
                              "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box",
                              "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"
                  ),
                  new ConfigLabel("Miscellaneous"),
                  always_disables_back_slot = new BoolConfigVariant("always_disables_back_slot", false)
      };

      private final ConfigLabel GAMERULE_LABEL = new ConfigLabel("Gamerules");
      public final HashMap<Gamerules, BoolConfigVariant> gamerules = Gamerules.getBoolConfig();

      @Override
      public String getPath() {
            return "-common";
      }

      @Override
      public Collection<ConfigLine> getLines() {
            ArrayList<ConfigLine> lines = new ArrayList<>(List.of(LINES));
            lines.add(GAMERULE_LABEL);
            lines.addAll(gamerules.values());
            return lines;
      }

      @Deprecated(since = "0.27-v2")
      public void read() {
            HashSet<Item> blacklistItems = Constants.BLACKLIST_ITEMS;
            if (!blacklistItems.isEmpty()) {
                  blacklist_items.set(blacklistItems);
                  usesOldDataPackConfig = true;
            }
            HashSet<Item> chestplateDisabled = Constants.CHESTPLATE_DISABLED;
            if (!chestplateDisabled.isEmpty()) {
                  disable_chestplate.set(chestplateDisabled);
                  usesOldDataPackConfig = true;
            }
            HashSet<Item> disablesBackSlot = Constants.DISABLES_BACK_SLOT;
            if (!disablesBackSlot.isEmpty()) {
                  disables_back_slot.set(disablesBackSlot);
                  usesOldDataPackConfig = true;
            }
            HashSet<Item> elytraItems = Constants.ELYTRA_ITEMS;
            if (!elytraItems.isEmpty()) {
                  elytra_items.set(elytraItems);
                  usesOldDataPackConfig = true;
            }

            if (usesOldDataPackConfig) {
                  Constants.LOG.warn("The \"modify\" folder your Data-Pack is outdated");
                  Constants.LOG.warn("Use config/" + Constants.MOD_ID + "-common.json5 to modify these lists" );
                  Constants.LOG.warn("The Data Pack's values will be used for now but will not work in later versions" );
            }

            IConfig.super.read();
      }
}
