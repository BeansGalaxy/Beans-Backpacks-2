package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.nbt.CompoundTag;

public class LocalData {
      public String key;
      public int color;
      public CompoundTag trim;

      public LocalData(String key, int color, CompoundTag trim) {
            this.key = key;
            this.color = color;
            this.trim = trim == null ? new CompoundTag() : trim;
      }

      public Traits traits() {
            return Constants.TRAITS_MAP.get(key);
      }

      public String name() {
            return traits().name;
      }

      public Kind kind() {
            return traits().kind;
      }

      public int maxStacks() {
            return traits().maxStacks;
      }
}
