package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ServerSave;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Traits {
      public static void register(String key, Traits traits) {
            if (key.isEmpty() || key.equals("pot"))
                  return;

            Constants.TRAITS_MAP.put(key, traits);
      }

      public static Traits get(String key) {
            if (key.equals("pot"))
                  return new Traits(Kind.POT, 999);

            Traits traits = Constants.TRAITS_MAP.get(key);
            if (traits == null)
            {
                  Constants.LOG.info("Requested Traits for key: \"" + key + "\" but Traits returned null");
                  return new Traits(Kind.UPGRADED, 0);
            }
            return traits;
      }

      public static String keyFromIngredients(Item material, Item binder) {
            for (String key: Constants.TRAITS_MAP.keySet()) {
                  Traits traits = get(key);
                  if (traits.material == material && traits.binder == binder)
                        return key;
            }
            return null;
      }

      public static void clear() {
            Constants.TRAITS_MAP.clear();
      }

      protected Traits(Kind kind, int maxStacks) {
            this.name = "";
            this.kind = kind;
            this.maxStacks = maxStacks;
      }

      public Item template;
      public Item base;
      public Item material;
      public Item binder;

      public String name;
      public Kind kind;
      public int maxStacks;

      public Traits(Raw raw) {
            this.template = Constants.itemFromString(raw.template);
            this.base = Constants.itemFromString(raw.base);
            this.material = Constants.itemFromString(raw.material);
            this.binder = Constants.itemFromString(raw.binder);
            this.name = raw.name;
            this.kind = Kind.fromName(raw.kind);
            this.maxStacks = raw.max_stacks;
      }

      public static class Raw {
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

      public static class LocalData {
            public static final LocalData POT = new LocalData("pot");
            public static final LocalData EMPTY = new LocalData("");
            public static final LocalData ENDER = new LocalData("ender");

            public String key;
            public int color = 0xFFFFFF;
            public CompoundTag trim = new CompoundTag();
            public Component hoverName = Component.empty();

            public int damage = 0;

            public LocalData(String key, int color, CompoundTag trim, Component hoverName, int damage) {
                  this.key = key;
                  this.color = color;
                  this.trim = trim == null ? new CompoundTag(): trim;
                  this.hoverName = hoverName == null ? Component.empty(): hoverName;
                  this.damage = damage;
            }

            LocalData(String key) {
                  this.key = key;
            }

            public boolean isPot() {
                  return key.equals("pot");
            }

            public static LocalData fromstack(ItemStack stack) {
                  Kind kind = Kind.fromStack(stack);
                  if (Kind.POT.is(kind))
                        return POT;

                  if (Kind.ENDER.is(kind))
                        return ENDER;

                  if (!Kind.isBackpack(stack))
                        return EMPTY;


                  CompoundTag display = stack.getOrCreateTagElement("display");

                  String key = display.getString("key");
                  int itemColor = stack.getItem() instanceof DyeableLeatherItem dyable ? dyable.getColor(stack) : 0xFFFFFF;
                  CompoundTag trim = stack.getTagElement("Trim");
                  Component hoverName = stack.hasCustomHoverName() ? stack.getHoverName(): Component.empty();
                  int damage = stack.getDamageValue();

                  return new LocalData(key, itemColor, trim, hoverName, damage);
            }

            public Traits traits() {
                  return get(key);
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
}
