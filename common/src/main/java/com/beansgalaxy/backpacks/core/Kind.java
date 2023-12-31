package com.beansgalaxy.backpacks.core;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum Kind {
      LEATHER(Services.REGISTRY.getLeather()),
      METAL(Services.REGISTRY.getMetal()),
      UPGRADED(Services.REGISTRY.getUpgraded()),
      POT(Items.DECORATED_POT.asItem());

      final Item item;

      Kind(Item item) {
            this.item = item;
      }

      public static boolean isBackpack(ItemStack backpackStack) {
            Item item = backpackStack.getItem();
            return item instanceof BackpackItem;
      }

      public static boolean isWearable(ItemStack stack) {
            for(Kind kind: Kind.values())
                  if(stack.is(kind.getItem()))
                        return true;
            return Constants.CHESTPLATE_DISABLED.contains(stack.getItem());
      }

      public static boolean isStorage(ItemStack stack) {
            if (stack.isEmpty())
                  return false;

            if (stack.is(Items.DECORATED_POT))
                  return true;

            CompoundTag tag = stack.getTag();
            if (tag == null || !isBackpack(stack)) return false;

            String key = tag.getCompound("display").getString("key");
            return Traits.get(key).maxStacks > 0;
      }

      public boolean isTrimmable() {
            return this == Kind.METAL || this == Kind.UPGRADED;
      }


      public static Kind fromStack(ItemStack stack) {
            for(Kind kind: Kind.values())
                  if (kind.is(stack))
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
}
