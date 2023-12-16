package com.beansgalaxy.backpacks.general;

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
      POT(Items.DECORATED_POT.asItem()),
      ELYTRA(Items.ELYTRA.asItem());

      final Item item;

      Kind(Item item) {
            this.item = item;
      }

      public static boolean isBackpack(ItemStack backpackStack) {
            Item item = backpackStack.getItem();;
            return item instanceof BackpackItem;
      }

      public static boolean isWearable(ItemStack stack) {
            for(Kind kind: Kind.values())
                  if(stack.is(kind.getItem()))
                        return true;
            return false;
      }

      public static boolean isStorage(ItemStack stack) {
            CompoundTag tag = stack.getTag();
            return tag != null && tag.getCompound("display").getInt("max_stacks") > 0;
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
            return Kind.valueOf(string);
      }

      public boolean is(ItemStack backStack) {
            return backStack.is(item);
      }
}
