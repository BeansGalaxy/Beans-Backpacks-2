package com.beansgalaxy.backpacks.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BackpackTooltip implements TooltipComponent {
      public NonNullList<ItemStack> itemStacks = NonNullList.create();

      public BackpackTooltip(NonNullList<ItemStack> items) {
            itemStacks.clear();
            items.forEach(item -> itemStacks.add(item.copy()));

            if (!itemStacks.isEmpty())
            {
                  for (int j = 0; j < itemStacks.size(); j++)
                  {
                        ItemStack itemStack = itemStacks.get(j);
                        int count = itemStacks.stream()
                                    .filter(itemStacks -> itemStacks.is(itemStack.getItem()) && !itemStack.equals(itemStacks))
                                    .mapToInt(itemStacks -> itemStacks.copyAndClear().getCount()).sum();
                        itemStack.setCount(count + itemStack.getCount());
                        itemStacks.removeIf(ItemStack::isEmpty);
                  }
            }
      }
}
