package com.beansgalaxy.backpacks.general;

import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.world.item.Item;

public enum Kind {
      LEATHER(Services.REGISTRY.getLeather()),
      METAL(Services.REGISTRY.getMetal()),
      UPGRADED(Services.REGISTRY.getUpgraded());

      final Item item;

      Kind(Item item) {
            this.item = item;
      }

      public Item getItem() {
            return item;
      }

      public static Kind fromName(String string) {
            return Kind.valueOf(string);
      }
}
