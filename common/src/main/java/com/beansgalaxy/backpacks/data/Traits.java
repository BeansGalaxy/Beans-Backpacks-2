package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class Traits {
      public static final Traits EMPTY = new Traits("Err", true, "none", null, 0, "clay");
      public static final Traits LEATHER;
      public static final Traits METAL;
      public static final Traits ENDER;
      public static final Traits WINGED;
      public static final Traits POT;
      public static final Traits CAULDRON;
      public static final Traits BIG_BUNDLE;

      public static final Function<Integer, Color> IGNORE_COLOR = i -> new Color(0xFFFFFF);

      public final String name;
      private final Supplier<Integer> maxStacks;
      public final boolean fireResistant;
      public final String button;
      public final ArmorMaterial material;
      public final Sound sound;

      public Traits(String name, boolean fireResistant, String button, String material, int maxStacks, String sound) {
            this(name, fireResistant, button, material, getOverrideableMaxStack(name, maxStacks), Sound.from(sound));
      }

      public Traits(String name, boolean fireResistant, String button, String material, Supplier<Integer> maxStacks, Sound sound) {
            this.name = name;
            this.maxStacks = maxStacks;
            this.fireResistant = fireResistant;
            this.button = button;
            this.material = getMaterial(material);
            this.sound = sound;
      }

      public Traits(String key, CompoundTag tag) {
            this.name = tag.getString("name");
            this.sound = Sound.valueOf(tag.getString("sound"));
            this.maxStacks = getOverrideableMaxStack(key, tag.getInt("max_stacks"));
            this.fireResistant = tag.getBoolean("fire_resistant");
            this.button = tag.getString("button");
            this.material = getMaterial(tag.getString("material"));
      }

      public static Supplier<Integer> getOverrideableMaxStack(String backpack_id, int max_stacks) {
            return () -> {
                  HashMap<String, Integer> map = ServerSave.CONFIG.data_driven_overrides.get();
                  return map.getOrDefault(backpack_id, max_stacks);
            };
      }

      public int getMaxStacks() {
            return maxStacks.get();
      }

      static ArmorMaterials getMaterial(String key) {
            if (Constants.isEmpty(key))
                  return ArmorMaterials.LEATHER;

            for (ArmorMaterials armorMaterial : ArmorMaterials.values()) {
                  String armor = armorMaterial.getName();

                  if (armor.equals(key))
                        return armorMaterial;
            }
            return ArmorMaterials.LEATHER;
      }

      public static void clear() {
            Constants.TRAITS_MAP.clear();
      }

      public CompoundTag toTag() {
            CompoundTag data = new CompoundTag();
            data.putString("name", name);
            data.putInt("max_stacks", maxStacks.get());
            data.putBoolean("fire_resistant", fireResistant);
            data.putString("button", button);
            data.putString("material", material.getName());
            data.putString("sound", sound.name());

            return data;
      }

      public static void register(String key, Traits traits) {
            if (Constants.isEmpty(key))
                  return;

            Constants.TRAITS_MAP.put(key, traits);
      }

      public static Traits get(String key) {
            Traits metal = Constants.TRAITS_MAP.get("METAL");
            if (Constants.isEmpty(key)) {;
                  return metal;
            }

            Traits traits = Constants.TRAITS_MAP.get(key);
            if (traits != null)
                  return traits;

            return metal;
      }

      public static Component getName(LocalData traits) {
            String key = traits.backpack_id;
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
            public final String backpack_id;
            public int color = 0xFFFFFF;
            private CompoundTag trim = new CompoundTag();
            public Component hoverName = Component.empty();

            public LocalData(String backpack_id, Kind kind, int color, CompoundTag trim, Component hoverName) {
                  this.backpack_id = backpack_id == null ? "": backpack_id;
                  this.kind = kind;
                  this.color = color;
                  this.trim = trim == null ? new CompoundTag(): trim;
                  this.hoverName = hoverName == null ? Component.empty(): hoverName;
            }

            private LocalData(Kind kind) {
                  this.backpack_id = "";
                  this.kind = kind;
            }

            public LocalData(CompoundTag tag) {
                  String kindString = tag.getString("kind");
                  this.kind = Kind.fromName(kindString);
                  this.backpack_id = tag.getString("backpack_id");
                  if (tag.contains("empty") && tag.getBoolean("empty"))
                        this.isEmpty = true;
                  else {
                        this.color = tag.getInt("color");
                        this.trim = tag.getCompound("Trim");
                        this.hoverName = Component.Serializer.fromJson(tag.getString("name"));
                  }
            }

            public LocalData() {
                  this.backpack_id = "";
                  this.kind = Kind.METAL;
                  this.isEmpty = true;
            }

            public CompoundTag toNBT() {
                  CompoundTag data = new CompoundTag();
                  data.putString("kind", kind.name());
                  data.putString("backpack_id", backpack_id);
                  if (isEmpty())
                        data.putBoolean("empty", true);
                  else {
                        data.putInt("color", color);
                        data.put("Trim", getTrim());
                        data.putString("name", Component.Serializer.toJson(hoverName));
                  }
                  return data;
            }

            public CompoundTag getTrim() {
                  return trim;
            }

            public static LocalData fromStack(ItemStack stack, Player player) {
                  return fromStack(stack, player.level(), player.getUUID());
            }

            public static LocalData fromStack(ItemStack stack, Level level, UUID fallback) {
                  Kind kind = Kind.fromStack(stack);
                  if (kind == null)
                        return EMPTY;

                  Component hoverName = stack.hasCustomHoverName() ? stack.getHoverName(): Component.empty();
                  switch (kind) {
                        case UPGRADED -> {
                              CompoundTag trim = stack.getTagElement("Trim");
                              return new LocalData("null", Kind.METAL, 0xFFFFFF, trim, hoverName);
                        }
                        case METAL -> {
                              CompoundTag tag = stack.getTag();
                              CompoundTag trim = stack.getTagElement("Trim");
                              if (tag == null || !tag.contains("backpack_id"))
                                    return new LocalData("", Kind.METAL, 0xFFFFFF, trim, hoverName);

                              String key = tag.getString("backpack_id");
                              return new LocalData(key, Kind.METAL, 0xFFFFFF, trim, hoverName);
                        }
                        case LEATHER -> {
                              int itemColor = stack.getItem() instanceof DyeableLeatherItem dyable ? dyable.getColor(stack) : 0xFFFFFF;
                              return new LocalData("", Kind.LEATHER, itemColor, null, hoverName);
                        }
                        case WINGED -> {
                              int itemColor = stack.getItem() instanceof DyeableLeatherItem dyable ? dyable.getColor(stack) : 0xFFFFFF;
                              return new LocalData("", Kind.WINGED, itemColor, null, hoverName);
                        }
                        case ENDER -> {
                              CompoundTag tag = stack.getTag();

                              UUID uuid = tag != null && tag.contains("owner")
                                          ? tag.getUUID("owner")
                                          : fallback;

                              return new LocalData("", Kind.ENDER, 0xFFFFFF, null, hoverName) {
                                    @Override
                                    public CompoundTag getTrim() {
                                          return EnderStorage.getEnderData(uuid, level).getTrim();
                                    }
                              };
                        }
                        case CAULDRON -> {
                              return CAULDRON;
                        }
                        case POT -> {
                              return POT;
                        }
                        case BIG_BUNDLE -> {
                              CompoundTag trim = stack.getTagElement("Trim");
                              int itemColor = stack.getItem() instanceof DyableBackpack dyable ? dyable.getBundleColor(stack) : 0xFFFFFF;
                              return new LocalData("", Kind.BIG_BUNDLE, itemColor, trim, hoverName);
                        }
                  }

                  return EMPTY;
            }

            public Traits traits() {
                  return kind.traits(backpack_id);
            }

            public String name() {
                  return traits().name;
            }

            public int maxStacks() {
                  return traits().getMaxStacks();
            }

            public boolean fireResistant() {
                  return traits().fireResistant;
            }

            public String button() {
                  return traits().button;
            }

            public boolean isEmpty() {
                  return this == EMPTY || isEmpty;
            }

            public boolean isStorage() {
                  return !isEmpty && maxStacks() > 0;
            }

            public ArmorMaterial material() {
                  return traits().material;
            }

            public Sound sound(){
                  return traits().sound;
            }
      }

      static {
            LEATHER = new Traits("Backpack",
                        false, "minecraft:gold", "leather",
                        ServerSave.CONFIG.leather_max_stacks::get,
                        Sound.SOFT);

            METAL = new Traits("Iron Backpack",
                        false, "minecraft:diamond", "iron",
                        ServerSave.CONFIG.metal_max_stacks::get,
                        Sound.HARD);

            ENDER = new Traits("Ender Backpack",
                        false, "none", "turtle",
                        ServerSave.CONFIG.ender_max_stacks::get,
                        Sound.VWOOMP);

            WINGED = new Traits("Winged Backpack",
                        false, "minecraft:gold", "leather",
                        ServerSave.CONFIG.winged_max_stacks::get,
                        Sound.CRUNCH);

            POT = new Traits("Decorated Pot",
                        false, "none", null,
                        ServerSave.CONFIG.pot_max_stacks::get,
                        Sound.CLAY);

            CAULDRON = new Traits("Cauldron",
                        false, "none", null,
                        ServerSave.CONFIG.cauldron_max_buckets::get,
                        Sound.HARD);

            BIG_BUNDLE = new Traits("Big Bundle",
                        false, "minecraft:emerald", "leather",
                        () -> ServerSave.CONFIG.leather_max_stacks.get() + 1,
                        Sound.SOFT);
      }

      public enum Sound {
            SOFT,
            HARD,
            VWOOMP,
            CRUNCH,
            CLAY;

            public static Sound from(String sound) {
                  try {
                        return Sound.valueOf(sound.toUpperCase());
                  } catch (IllegalArgumentException ignored) {
                        return Sound.HARD;
                  }
            }
      }
}
