package com.beansgalaxy.backpacks.data.config;

import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public enum Gamerules {
      UNBIND_ENDER_ON_DEATH(Services.CONFIG.getBoolConfig(Config.UNBIND_ENDER_ON_DEATH), "unbindEnderOnDeath"),
      LOCK_ENDER_OFFLINE(Services.CONFIG.getBoolConfig(Config.LOCK_ENDER_OFFLINE), "lockEnderOffline"),
      LOCK_BACKPACK_OFFLINE(Services.CONFIG.getBoolConfig(Config.LOCK_BACKPACK_OFFLINE), "lockBackpackOffline"),
      LOCK_BACKPACK_NOT_OWNER(Services.CONFIG.getBoolConfig(Config.LOCK_BACKPACK_NOT_OWNER), "lockBackpackNotOwner"),
      KEEP_BACK_SLOT(Services.CONFIG.getBoolConfig(Config.KEEP_BACK_SLOT), "keepBackSlot");

      final boolean defaultValue;
      final String readable;

      Gamerules(boolean defaultValue, String readable) {
            this.defaultValue = defaultValue;
            this.readable = readable;
      }

      public String readable() {
            return readable;
      }

      public static CompoundTag toNBT(CompoundTag tag, HashMap<Gamerules, Boolean> values) {
            CompoundTag configTag = new CompoundTag();
            for (Gamerules rule : values.keySet()) {
                  Boolean b = values.get(rule);
                  if (b != rule.defaultValue)
                        configTag.putBoolean(rule.name(), b);
            }

            tag.put("Config", configTag);
            return tag;
      }

      public static HashMap<Gamerules, Boolean> fromNBT(CompoundTag tag) {
            CompoundTag configTag;
            HashMap<Gamerules, Boolean> values = new HashMap<>();

            if (tag.contains("Config")) {
                  configTag = tag.getCompound("Config");
                  for (Gamerules rule : Gamerules.values()) {
                        String name = rule.name();
                        boolean value = rule.defaultValue;

                        if (configTag.contains(name))
                              value = configTag.getBoolean(name);

                        values.put(rule, value);
                  }
            }

            return values;
      }


      public static HashMap<Gamerules, Boolean> getDefaults() {
            HashMap<Gamerules, Boolean> defaults = new HashMap<>();
            for (Gamerules value : values())
                  defaults.put(value, value.defaultValue);
            return defaults;
      }
}
