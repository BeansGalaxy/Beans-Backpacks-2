package com.beansgalaxy.backpacks.core;

import net.minecraft.nbt.CompoundTag;

public class LocalData {
      public static final LocalData POT = new LocalData(true);
      public static final LocalData EMPTY = new LocalData("");

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
      LocalData(String key) {
            this.key = key;}

      public Traits traits() {
            return Traits.get(key);
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
