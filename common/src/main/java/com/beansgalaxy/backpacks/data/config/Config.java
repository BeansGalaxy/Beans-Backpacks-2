package com.beansgalaxy.backpacks.data.config;

import java.util.HashMap;
import java.util.Map;

public enum Config {

      // Common Config
      LEATHER_MAX_STACKS(      new Entry<>(4)),
      ENDER_MAX_STACKS(        new Entry<>(4)),
      WINGED_MAX_STACKS(       new Entry<>(4)),
      METAL_MAX_STACKS(        new Entry<>(7)),
      POT_MAX_STACKS(          new Entry<>(128)),
      CAULDRON_MAX_BUCKETS(    new Entry<>(24)),
      UNBIND_ENDER_ON_DEATH(   new Entry<>(false)),
      LOCK_ENDER_OFFLINE(      new Entry<>(false)),
      LOCK_BACKPACK_OFFLINE(   new Entry<>(false)),
      LOCK_BACKPACK_NOT_OWNER( new Entry<>(false)),
      KEEP_BACK_SLOT(          new Entry<>(false)),

      // Client Config
      MENU_VISIBILITY         (new Entry<>(MenuVisibility.HIDE_ABLE)),
      INSTANT_PLACE           (new Entry<>(false));

      public static final int MAX_STACKS_RANGE = 32;
      public static final int MAX_SPECIAL_RANGE = 128;
      public static final int MAX_ENDER_RANGE = 8;

      final Entry<?> defaultValue;

      Config(Entry<?> defaultValue) {
            this.defaultValue = defaultValue;
      }

      public <T> T get(Class<T> clazz) {
            return clazz.cast(defaultValue.defaultValue());
      }

      public record Entry<T>(T defaultValue) {

      }
}
