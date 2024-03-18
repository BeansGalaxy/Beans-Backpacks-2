package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.entity.Kind;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class Traits {
      public static final Traits EMPTY = new Traits("Err", 0, true, Button.NETHERITE);
      public static final Traits LEATHER = new Traits("Backpack", 4, false, Button.GOLD);
      public static final Traits IRON = new Traits("Iron Backpack", 7, false, Button.DIAMOND);
      public static final Traits ENDER = new Traits("Ender Backpack", 4, false, Button.NONE);
      public static final Traits WINGED = new Traits("Winged Backpack", 4, false, Button.GOLD);
      public static final Traits POT = new Traits("Decorated Pot", 999, false, Button.NONE);
      public static final Traits CAULDRON = new Traits("Cauldron", 999, false, Button.NONE);

      public static final BiFunction<Kind, String, ResourceLocation> DEFAULT_RESOURCE =
                  (kind, key) -> new ResourceLocation(Constants.MOD_ID, "textures/entity/" + kind.name().toLowerCase() + ".png");

      public final String name;
      public final int maxStacks;
      public final boolean fireResistant;
      public final Button button;

      public Traits(String name, int maxStacks, boolean fireResistant, Button button) {
            this.name = name;
            this.maxStacks = maxStacks;
            this.fireResistant = fireResistant;
            this.button = button;
      }

      public Traits(CompoundTag tag) {
            this.maxStacks = tag.getInt("max_stacks");
            this.name = tag.getString("name");
            this.fireResistant = tag.getBoolean("fire_resistant");
            this.button = Button.fromName(tag.getString("button"));
      }

      public static void clear() {
            Constants.TRAITS_MAP.clear();
      }

      public CompoundTag toTag() {
            CompoundTag data = new CompoundTag();
            data.putString("name", name);
            data.putInt("max_stacks", maxStacks);
            data.putBoolean("fire_resistant", fireResistant);
            data.putString("button", button.name());

            return data;
      }

      public static void register(String key, Traits traits) {
            if (Constants.isEmpty(key))
                  return;

            Constants.TRAITS_MAP.put(key, traits);
      }

      public static String last_troubled_key = null;
      public static Traits get(String key) {
            if (Constants.isEmpty(key)) {
                  return IRON;
            }
            Traits traits = Constants.TRAITS_MAP.get(key);
            if (traits == null)
            {
                  if (!key.equals(last_troubled_key)) {
                        Constants.LOG.warn("Requested Traits for key: \"" + key + "\" but Traits returned null");
                        last_troubled_key = key;
                  }
                  return EMPTY;
            }
            return traits;
      }

      public static Component getName(LocalData traits) {
            String key = traits.key;
            if (Constants.isEmpty(key))
                  return Component.translatableWithFallback("item.beansbackpacks.metal_backpack", "Iron Backpack");
            return Component.translatableWithFallback("tooltip.beansbackpacks.name." + key, traits.name());
      }

      public boolean isEmpty() {
            return this == EMPTY;
      }

      public static ItemStack toStack(String backpackID) {
            ItemStack stack = Kind.METAL.getItem().getDefaultInstance();
            stack.getOrCreateTag().putString("backpack_id", backpackID);
            return stack;
      }

      public static class LocalData {
            public static final LocalData EMPTY = new LocalData();
            public static final LocalData POT = new LocalData(Kind.POT);
            public static final LocalData CAULDRON = new LocalData(Kind.CAULDRON);

            private boolean isEmpty;
            public final Kind kind;
            public final String key;
            public int color = 0xFFFFFF;
            private CompoundTag trim = new CompoundTag();
            public Component hoverName = Component.empty();

            public LocalData(String key, Kind kind, int color, CompoundTag trim, Component hoverName, int damage) {
                  this.key = key == null ? "": key;
                  this.kind = kind;
                  this.color = color;
                  this.trim = trim == null ? new CompoundTag(): trim;
                  this.hoverName = hoverName == null ? Component.empty(): hoverName;
            }

            private LocalData(Kind kind) {
                  this.key = "";
                  this.kind = kind;
            }

            public LocalData(CompoundTag tag) {
                  String kindString = tag.getString("kind");
                  this.kind = Kind.fromName(kindString);
                  this.key = tag.getString("backpack_id");
                  if (tag.contains("empty") && tag.getBoolean("empty"))
                        this.isEmpty = true;
                  else {
                        this.color = tag.getInt("color");
                        this.trim = tag.getCompound("Trim");
                        this.hoverName = Component.Serializer.fromJson(tag.getString("name"));
                  }
            }

            public LocalData() {
                  this.key = "";
                  this.kind = Kind.METAL;
                  this.isEmpty = true;
            }

            public CompoundTag toNBT() {
                  CompoundTag data = new CompoundTag();
                  data.putString("kind", kind.name());
                  data.putString("backpack_id", key);
                  if (isEmpty())
                        data.putBoolean("empty", true);
                  else {
                        data.putInt("color", color);
                        data.put("Trim", trim);
                        data.putString("name", Component.Serializer.toJson(hoverName));
                  }
                  return data;
            }

            public CompoundTag getTrim() {
                  return trim;
            }

            public static LocalData fromStack(ItemStack stack) {
                  Kind kind = Kind.fromStack(stack);
                  if (kind == null)
                        return EMPTY;

                  Component hoverName = stack.hasCustomHoverName() ? stack.getHoverName(): Component.empty();
                  switch (kind) {
                        case UPGRADED -> {
                              CompoundTag tag = stack.getTag();
                              CompoundTag trim = stack.getTagElement("Trim");
                              if (tag == null || !tag.contains("backpack_id"))
                                    return new LocalData("netherite", Kind.METAL, 0xFFFFFF, trim, hoverName, 0);

                              String key = tag.getString("backpack_id");
                              return new LocalData(key, Kind.METAL, 0xFFFFFF, trim, hoverName, 0);
                        }
                        case METAL -> {
                              CompoundTag tag = stack.getTag();
                              CompoundTag trim = stack.getTagElement("Trim");
                              if (tag == null || !tag.contains("backpack_id"))
                                    return new LocalData("", Kind.METAL, 0xFFFFFF, trim, hoverName, 0);

                              String key = tag.getString("backpack_id");
                              return new LocalData(key, Kind.METAL, 0xFFFFFF, trim, hoverName, 0);
                        }
                        case LEATHER -> {
                              int itemColor = stack.getItem() instanceof DyeableLeatherItem dyable ? dyable.getColor(stack) : 0xFFFFFF;
                              return new LocalData("", Kind.LEATHER, itemColor, null, hoverName, 0);
                        }
                        case WINGED -> {
                              int damage = stack.getDamageValue();
                              int itemColor = stack.getItem() instanceof DyeableLeatherItem dyable ? dyable.getColor(stack) : 0xFFFFFF;
                              return new LocalData("", Kind.WINGED, itemColor, null, hoverName, damage);
                        }
                        case ENDER -> {
                              CompoundTag trim = stack.getTagElement("Trim");
                              return new LocalData("", Kind.ENDER, 0xFFFFFF, trim, hoverName, 0);
                        }
                        case CAULDRON -> {
                              return CAULDRON;
                        }
                        case POT -> {
                              return POT;
                        }
                  }

                  return EMPTY;
            }

            public boolean isSpecial() {
                  return Kind.POT.is(kind) || Kind.CAULDRON.is(kind);
            }

            public Traits traits() {
                  return kind.traits(key);
            }

            public String name() {
                  return traits().name;
            }

            public int maxStacks() {
                  return traits().maxStacks;
            }

            public boolean fireResistant() {
                  return traits().fireResistant;
            }

            public Button button() {
                  return traits().button;
            }

            public boolean isEmpty() {
                  return this == EMPTY || isEmpty;
            }

            public boolean isStorage() {
                  return !isEmpty && maxStacks() > 0;
            }
      }

      public enum Button {
            GOLD,
            AMETHYST,
            DIAMOND,
            NETHERITE,
            NONE{
                  @Override public ResourceLocation getResource() {
                        return null;
                  }
            };

            public ResourceLocation getResource() {
                  return new ResourceLocation(Constants.MOD_ID, "textures/entity/overlay/" + this.name().toLowerCase() + ".png");
            }

            public static Button fromName(String name) {
                  for (Button value : Button.values()) {
                        if (value.name().equals(name.toUpperCase()))
                              return value;
                  }
                  return DIAMOND;
            }
      }
}
