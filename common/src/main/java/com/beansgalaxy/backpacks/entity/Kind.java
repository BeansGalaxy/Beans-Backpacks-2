package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Function;

public enum Kind {
      LEATHER(  Services.REGISTRY.getLeather(),  key -> Traits.LEATHER,  data -> Kind.getAppendedName("", "Backpack")),
      METAL(    Services.REGISTRY.getMetal(),    Traits::get,            Traits::getName),
      UPGRADED( Services.REGISTRY.getUpgraded(), Traits::get,            Traits::getName),
      WINGED(   Services.REGISTRY.getWinged(),   key -> Traits.WINGED,   data -> Kind.getAppendedName("winged_", "Winged Backpack")),
      ENDER(    Services.REGISTRY.getEnder(),    key -> Traits.ENDER,    data -> Kind.getAppendedName("ender_", "Ender Backpack")),
      POT(      Items.DECORATED_POT.asItem(),    key -> Traits.POT,      data -> Component.empty()),
      CAULDRON( Items.CAULDRON.asItem(),         key -> Traits.CAULDRON, data -> Component.empty());

      final Item item;
      final Function<String, Traits> getTraits;
      private final Function<Traits.LocalData, Component> getName;

      Kind(Item item, Function<String, Traits> getTraits, Function<Traits.LocalData, Component> getName) {
            this.item = item;
            this.getTraits = getTraits;
            this.getName = getName;
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

            return Traits.IRON;
      }

      public Traits traits(String backpack_id) {
            return getTraits.apply(backpack_id);
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
                  if(item.equals(kind.getItem()))
                        return true;
            return Constants.CHESTPLATE_DISABLED.contains(item) || Constants.ELYTRA_ITEMS.contains(item);

      }

      public static boolean isWings(ItemStack backStack) {
            if (backStack.is(Services.REGISTRY.getWinged()))
                  return true;

            return Constants.ELYTRA_ITEMS.contains(backStack.getItem());
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("textures/entity/");
            if (!Constants.isEmpty(key) && Kind.is(this, Kind.METAL, Kind.UPGRADED))
                  stringBuilder.append("backpack/").append(key);
            else
                  stringBuilder.append(this.name().toLowerCase());

            if (!Constants.isEmpty(append))
                  stringBuilder.append(append);

            stringBuilder.append(".png");
            String location = stringBuilder.toString();
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

      public Component getName(Traits.LocalData traits) {
            return getName.apply(traits);
      }
}
