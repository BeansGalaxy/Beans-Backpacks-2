package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.inventory.PotInventory;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

public enum Kind {
      LEATHER(  Services.REGISTRY.getLeather(),  Traits.LEATHER,  DyableBackpack::shiftColor, data -> Kind.getAppendedName("", "Backpack")),
      METAL(    Services.REGISTRY.getMetal(),    Traits.METAL,    Traits.IGNORE_COLOR,        Traits::getName),
      UPGRADED( Services.REGISTRY.getUpgraded(), Traits.METAL,    Traits.IGNORE_COLOR,        Traits::getName),
      WINGED(   Services.REGISTRY.getWinged(),   Traits.WINGED,   WingedBackpack::shiftColor, data -> Kind.getAppendedName("winged_", "Winged Backpack")),
      ENDER(    Services.REGISTRY.getEnder(),    Traits.ENDER,    Traits.IGNORE_COLOR,        data -> Kind.getAppendedName("ender_", "Ender Backpack")),
      POT(      Items.DECORATED_POT.asItem(),    Traits.POT,      Traits.IGNORE_COLOR,        data -> Component.empty()),
      CAULDRON( Items.CAULDRON.asItem(),         Traits.CAULDRON, Traits.IGNORE_COLOR,        data -> Component.empty());

      final Item item;
      private final Function<Traits.LocalData, Component> getName;
      private final Function<Integer, Color> getColor;
      public final Traits defaultTraits;

      Kind(Item item, Traits defaultTraits, Function<Integer, Color> getColor, Function<Traits.LocalData, Component> getName) {
            this.item = item;
            this.defaultTraits = defaultTraits;
            this.getName = getName;
            this.getColor = getColor;
      }

      public static Traits getTraits(ItemStack stack) {
            Kind kind = Kind.fromStack(stack);
            if (kind == null)
                  return Traits.EMPTY;

            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("backpack_id")) {
                  String id = tag.getString("backpack_id");
                  return kind.traits(id);
            }

            return Traits.METAL;
      }

      public Traits traits(String backpack_id) {
            if (Kind.METAL.is(this))
                  return Traits.get(backpack_id);

            Traits traits = Constants.TRAITS_MAP.get(this.name());
            if (traits != null)
                  return traits;

            return defaultTraits;
      }

      public static boolean isBackpack(ItemStack backpackStack) {
            Item item = backpackStack.getItem();
            return item instanceof BackpackItem;
      }

      public static boolean isWearable(ItemStack stack) {
            return isWearable(stack.getItem());
      }

      public static boolean isWearable(Item item) {
            for(Kind kind: Kind.values())
                  if(item.equals(kind.getItem())) {
                        boolean cauldronDisabled = !(Kind.CAULDRON.is(kind) && CauldronInventory.getMaxSize() == 0);
                        boolean potDisabled = !(Kind.POT.is(kind) && PotInventory.getMaxSize() == 0);
                        return cauldronDisabled && potDisabled;
                  }
            return Constants.CHESTPLATE_DISABLED.contains(item) || isWearableElytra(item);
      }

      public static boolean isWings(ItemStack backStack) {
            if (backStack.is(Services.REGISTRY.getWinged()))
                  return true;

            return isWearableElytra(backStack.getItem());
      }

      public static boolean isWearableElytra(Item item) {
            return Constants.ELYTRA_ITEMS.contains(item) && !Services.COMPAT.isModLoaded(CompatHelper.ELYTRA_SLOT);
      }

      public static Kind fromStack(ItemStack stack) {
            for(Kind kind: Kind.values())
                  if (kind.is(stack))
                        return kind;
            return null;
      }

      public static Kind fromItem(ItemLike item) {
            for(Kind kind: Kind.values())
                  if (kind.item.equals(item.asItem()))
                        return kind;
            return null;
      }

      public Item getItem() {
            return item;
      }

      public static Kind fromName(String string) {
            for(Kind kind: Kind.values())
                  if (kind.name().equals(string))
                        return kind;

            return null;
      }

      public boolean is(ItemStack backStack) {
            return backStack.is(item);
      }

      public boolean is(Kind kind) {
            return this == kind;
      }

      public static boolean is(Kind kind, Kind... kinds) {
            return kind != null && Arrays.stream(kinds).anyMatch(in -> in == kind);
      }

      public ResourceLocation getAppendedResource(String key, String append) {
            StringBuilder string = new StringBuilder();
            string.append("textures/entity/");

            switch (this) {
                  case LEATHER -> string.append("leather/leather");
                  case METAL, UPGRADED -> {
                        if (!Constants.isEmpty(key))
                              string.append("backpack/").append(key);
                        else string.append("metal");
                  }
                  default -> string.append(this.name().toLowerCase());
            }

            if (!Constants.isEmpty(append))
                  string.append(append);

            string.append(".png");
            String location = string.toString();
            return new ResourceLocation(Constants.MOD_ID, location);
      }

      private static Component getAppendedName(String append, String fallback) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("item.beansbackpacks.");
            if (append != null && !append.isEmpty() && !append.isBlank())
                  stringBuilder.append(append.toLowerCase());
            stringBuilder.append("backpack");
            return Component.translatableWithFallback(stringBuilder.toString(), fallback);
      }

      public Color getShiftedColor(int color) {
            return getColor.apply(color);
      }

      public Component getName(Traits.LocalData traits) {
            return getName.apply(traits);
      }
}
