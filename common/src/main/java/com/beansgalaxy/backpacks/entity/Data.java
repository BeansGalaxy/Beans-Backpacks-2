package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Data {

      public String key;
      public Item material;
      public Item binder;
      public String name;
      public Kind kind;
      public int maxStacks;
      public int color;
      public CompoundTag trim;

      public Data(String key, String name, Kind kind, int maxStacks, int color, CompoundTag trim) {
            this.key = key;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
            this.color = color;
            this.trim = trim != null ? trim : new CompoundTag();
      }

      public Data(String key, Item material, Item binder, String name, Kind kind, int maxStacks) {
            this.key = key;
            this.material = material;
            this.binder = binder;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
            this.color = 0xFFFFFF;
            this.trim = new CompoundTag();
      }

      public Data(Data.Raw raw) {
            this.material = Constants.itemFromString(raw.material);
            this.binder = Constants.itemFromString(raw.binder);
            this.name = raw.name;
            this.kind = Kind.fromName(raw.kind);
            this.maxStacks = raw.max_stacks;
      }

      public class Raw {
            public String key;
            public String material;
            public String binder;
            public String name;
            public String kind;
            public int max_stacks;

            public Raw() {}
      }

      public CompoundTag toTag() {
            CompoundTag data = new CompoundTag();
            ResourceLocation material = BuiltInRegistries.ITEM.getKey(this.material);
            data.putString("material", material.getNamespace() + ":" + material.getPath());
            ResourceLocation binder = BuiltInRegistries.ITEM.getKey(this.binder);
            data.putString("binder", binder.getNamespace() + ":" + binder.getPath());
            data.putString("name", name);
            data.putString("kind", kind.name());
            data.putInt("max_stacks", maxStacks);

            return data;
      }

      public Data(CompoundTag tag) {
            material = Constants.itemFromString(tag.getString("material"));
            binder = Constants.itemFromString(tag.getString("binder"));
            name = tag.getString("name");
            kind = Kind.fromName(tag.getString("kind"));
            maxStacks = tag.getInt("max_stacks");
      }
}
