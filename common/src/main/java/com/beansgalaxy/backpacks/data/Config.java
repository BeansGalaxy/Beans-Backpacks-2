package com.beansgalaxy.backpacks.data;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

      public static CompoundTag toNBT(CompoundTag tag) {
            CompoundTag configTag = new CompoundTag();
            for (Config config : ServerSave.CONFIG.keySet()) {
                  Boolean b = ServerSave.CONFIG.get(config);
                  configTag.putBoolean(config.name(), b);
            }

            tag.put("Config", configTag);
            return tag;
      }

      public static CompoundTag fromNBT(CompoundTag tag) {
            CompoundTag configTag;
            if (tag.contains("Config"))
                  configTag = tag.getCompound("Config");
            else
                  configTag = new CompoundTag();

            for (Config config : values()) {
                  String name = config.name();
                  if (configTag.contains(name)) {
                        boolean b = configTag.getBoolean(name);
                        ServerSave.CONFIG.put(config, b);
                  }
            }

            tag.put("Config", configTag);
            return tag;
      }


      public static HashMap<Config, Boolean> getDefaults() {
            HashMap<Config, Boolean> defaults = new HashMap<>();
            for (Config value : values())
                  defaults.put(value, value.defaultValue);
            return defaults;
      }
}
