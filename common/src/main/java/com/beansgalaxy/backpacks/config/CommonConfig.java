package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.config.types.*;
import com.beansgalaxy.backpacks.data.config.Gamerules;

import java.util.*;

public class CommonConfig implements IConfig {
      public IntConfigVariant leather_max_stacks;
      public IntConfigVariant ender_max_stacks;
      public IntConfigVariant winged_max_stacks;
      public IntConfigVariant metal_max_stacks;
      public IntConfigVariant pot_max_stacks;
      public IntConfigVariant cauldron_max_buckets;

      private final ConfigLine[] LINES = new ConfigLine[] {
                  new ConfigLabel("Maximum Stacks"),
                  leather_max_stacks =    new IntConfigVariant("leather_max_stacks", 4, 1, 64),
                  ender_max_stacks =      new IntConfigVariant("ender_max_stacks",   4, 1, 8),
                  winged_max_stacks =     new IntConfigVariant("winged_max_stacks",  4, 1, 64),
                  metal_max_stacks =      new IntConfigVariant("metal_max_stacks",   7, 1, 64),
                  pot_max_stacks =        new IntConfigVariant("pot_max_stacks",   128, 0, 128),
                  cauldron_max_buckets = new IntConfigVariant("cauldron_max_buckets", 24, 0, 128)
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
}
