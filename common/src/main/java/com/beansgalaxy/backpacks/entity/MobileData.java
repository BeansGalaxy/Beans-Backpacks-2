package com.beansgalaxy.backpacks.entity;

import net.minecraft.nbt.CompoundTag;

public class MobileData {

      public String key;
      public String name;
      public Kind kind;
      public int maxStacks;
      public int color;
      public CompoundTag trim;

      public MobileData(String key, String name, Kind kind, int maxStacks, int color, CompoundTag trim) {
            this.key = key;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
            this.color = color;
            this.trim = trim != null ? trim : new CompoundTag();
      }

      public MobileData(String key, String name, Kind kind, int maxStacks) {
            this.key = key;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
            this.color = 0xFFFFFF;
            this.trim = new CompoundTag();
      }

      public class Raw {
            public String type;
            public String name;
            public String key;
            public String kind;
            public int max_stacks;
            public String material;
            public String binder;

            public Raw() {}
      }
}
