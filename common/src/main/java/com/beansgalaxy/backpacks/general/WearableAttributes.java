package com.beansgalaxy.backpacks.general;

import com.beansgalaxy.backpacks.items.BackpackItem;

public class WearableAttributes {
      public final Kind kind;
      public final int maxStacks;
      public final String texItem;
      public final String texEntity;

      public WearableAttributes(Kind kind, int maximum_stacks, String item_texture, String entity_texture) {
            this.kind = kind;
            maxStacks = maximum_stacks;
            texItem = item_texture;
            texEntity = entity_texture;
      }

      public Kind getKind() {
            return kind;
      }

      public int getMaxStacks() {
            return maxStacks;
      }

      public String getTexItem() {
            return texItem;
      }

      public String getTexEntity() {
            return texEntity;
      }
}
