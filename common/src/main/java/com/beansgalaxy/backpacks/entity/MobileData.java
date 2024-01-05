package com.beansgalaxy.backpacks.entity;

import net.minecraft.nbt.CompoundTag;

public class MobileData {

      public String key;
      public int maxStacks;
      public String name;
      public Kind kind;
      public int color;
      public CompoundTag trim;

      public MobileData(String key, String name, Kind kind, int maxStacks, Integer color, CompoundTag trim) {
            this.key = key;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
            this.color = color;
            this.trim = trim != null ? trim : new CompoundTag();
      }
}
