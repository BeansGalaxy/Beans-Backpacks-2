package com.beansgalaxy.backpacks.data.config;

import com.beansgalaxy.backpacks.config.CommonConfig;
import com.beansgalaxy.backpacks.config.types.BoolConfigVariant;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public enum Gamerules {
      UNBIND_ENDER_ON_DEATH("unbindEnderOnDeath", false),
      LOCK_ENDER_OFFLINE("lockEnderOffline", false),
      LOCK_BACKPACK_OFFLINE("lockBackpackOffline", false),
      LOCK_BACKPACK_NOT_OWNER("lockBackpackNotOwner", false),
      KEEP_BACK_SLOT("keepBackSlot", false);

      private final String readable;
      private final boolean defau;

      Gamerules(String readable, boolean defau) {
            this.readable = readable;
            this.defau = defau;
      }

      public static HashMap<Gamerules, BoolConfigVariant> getBoolConfig() {
            HashMap<Gamerules, BoolConfigVariant> map = new HashMap<>();
            for (Gamerules value : Gamerules.values()) {
                  map.put(value, new BoolConfigVariant(value.readable, value.defau));
            }
            return map;
      }

      public static HashMap<Gamerules, Boolean> mapConfig(CommonConfig config) {
            HashMap<Gamerules, Boolean> map = new HashMap<>();
            config.gamerules.forEach((gr, entry) -> {
                  map.put(gr, entry.get());
            });
            return map;
      }

      public String readable() {
            return readable;
      }

      public static CompoundTag toNBT(CompoundTag tag, HashMap<Gamerules, Boolean> values, CommonConfig config) {
            CompoundTag configTag = new CompoundTag();
            for (Gamerules rule : values.keySet()) {
                  Boolean b = values.get(rule);
                  if (b != config.gamerules.get(rule).get())
                        configTag.putBoolean(rule.name(), b);
            }

            tag.put("Config", configTag);
            return tag;
      }

      public static HashMap<Gamerules, Boolean> fromNBT(CompoundTag tag, CommonConfig config) {
            CompoundTag configTag;
            HashMap<Gamerules, Boolean> values = new HashMap<>();

            if (tag.contains("Config")) {
                  configTag = tag.getCompound("Config");
                  for (Gamerules rule : Gamerules.values()) {
                        String name = rule.name();

                        boolean value;
                        if (configTag.contains(name))
                              value = configTag.getBoolean(name);
                        else value = config.gamerules.get(rule).get();

                        values.put(rule, value);
                  }
            }

            return values;
      }
}
