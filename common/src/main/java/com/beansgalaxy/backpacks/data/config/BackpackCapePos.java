package com.beansgalaxy.backpacks.data.config;

public enum BackpackCapePos {
      ON_TOP(1),
      BELOW(2);
      public final byte index;

      BackpackCapePos(int index) {
            this.index = (byte) index;
      }

      public static BackpackCapePos fromIndex(int index) {
            for (BackpackCapePos value : BackpackCapePos.values()) {
                  if (value.index == index)
                        return value;
            }
            return ON_TOP;
      }
}
