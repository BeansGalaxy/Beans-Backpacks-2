package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Traits {
      public static void register(String key, Traits traits) {
            if (key.isEmpty())
                  return;

            Constants.TRAITS_MAP.put(key, traits);
      }

      public static Traits get(String key) {
            Traits traits = Constants.TRAITS_MAP.get(key);
            if (traits == null)
            {
                  Constants.LOG.info("Requested Traits for key: \"" + key + "\" but Traits returned null");
                  return new Traits();
            }
            return traits;
      }
      public static void clear() {
            Constants.TRAITS_MAP.clear();
      }

      private Traits() {
            name = "";
            kind = Kind.UPGRADED;
            maxStacks = 0;
      }

      public Item template;
      public Item base;
      public Item material;
      public Item binder;

      public String name;
      public Kind kind;
      public int maxStacks;

      public Traits(Traits.Raw raw) {
            this.template = Constants.itemFromString(raw.template);
            this.base = Constants.itemFromString(raw.base);
            this.material = Constants.itemFromString(raw.material);
            this.binder = Constants.itemFromString(raw.binder);
            this.name = raw.name;
            this.kind = Kind.fromName(raw.kind);
            this.maxStacks = raw.max_stacks;
      }

      public boolean isSmithing() {
            return binder == null;
      }

      public class Raw {
            public String template;
            public String base;
            public String material;
            public String binder;

            public String key;
            public String name;
            public String kind;
            public int max_stacks;
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

      public Traits(CompoundTag tag) {
            material = Constants.itemFromString(tag.getString("material"));
            binder = Constants.itemFromString(tag.getString("binder"));
            name = tag.getString("name");
            kind = Kind.fromName(tag.getString("kind"));
            maxStacks = tag.getInt("max_stacks");
      }
}
