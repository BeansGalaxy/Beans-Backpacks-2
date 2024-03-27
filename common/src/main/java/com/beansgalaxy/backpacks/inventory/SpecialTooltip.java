package com.beansgalaxy.backpacks.inventory;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

public class SpecialTooltip implements TooltipComponent {
      public final Item item;
      public final int amount;

      SpecialTooltip(Item item, int amount) {
            this.item = item;
            this.amount = amount;
      }

      public static class Pot extends SpecialTooltip {
            public Pot(Item item, int amount) {
                  super(item, amount);
            }
      }

      public static class Cauldron extends SpecialTooltip {
            public Cauldron(Item item, int amount) {
                  super(item, amount);
            }
      }
}
