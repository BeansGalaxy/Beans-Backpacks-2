package com.beansgalaxy.backpacks.data;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public enum Config {
      UNBIND_ENDER_ON_DEATH(false, "unbindEnderOnDeath"),
      ENDER_LOCK_LOGGED_OFF(false, "lockEnderWhenLoggedOff");

      final boolean defaultValue;
      final String readable;

      Config(boolean defaultValue, String readable) {
            this.defaultValue = defaultValue;
            this.readable = readable;
      }

      public String readable() {
            return readable;
      }

      public static CompoundTag toNBT(CompoundTag tag, HashMap<Config, Boolean> values) {
            CompoundTag configTag = new CompoundTag();
            for (Config config : values.keySet()) {
                  Boolean b = values.get(config);
                  configTag.putBoolean(config.name(), b);
            }

            tag.put("Config", configTag);
            return tag;
      }

      public static HashMap<Config, Boolean> fromNBT(CompoundTag tag) {
            CompoundTag configTag;
            HashMap<Config, Boolean> values = new HashMap<>();

            if (tag.contains("Config")) {
                  configTag = tag.getCompound("Config");
                  for (Config value : Config.values()) {
                        String name = value.name();
                        boolean isEnabled = value.defaultValue;

                        if (configTag.contains(name))
                              isEnabled = configTag.getBoolean(name);

                        values.put(value, isEnabled);
                  }
            }

            return values;
      }


      public static HashMap<Config, Boolean> getDefaults() {
            HashMap<Config, Boolean> defaults = new HashMap<>();
            for (Config value : values())
                  defaults.put(value, value.defaultValue);
            return defaults;
      }
}
