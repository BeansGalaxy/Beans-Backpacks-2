package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.nbt.CompoundTag;

public class LocalData {
      public static final LocalData POT = new LocalData(true);

      public String key;
      public int color;
      public CompoundTag trim;
      private boolean isPot;

      public LocalData(String key, int color, CompoundTag trim) {
            this.key = key;
            this.color = color;
            this.trim = trim == null ? new CompoundTag() : trim;
      }

      LocalData(boolean isPot) {
            this.isPot = isPot;
      }

      public Traits traits() {
            return Constants.TRAITS_MAP.get(key);
      }

      public String name() {
            return traits().name;
      }

      public Kind kind() {
            return isPot ? Kind.POT : traits().kind;
      }

      public int maxStacks() {
            return isPot ? 999 : traits().maxStacks;
      }
}
