package com.beansgalaxy.backpacks.data.config;

public enum Config {

      // Common Config
      LEATHER_MAX_STACKS(     new Entry<>(4)),
      ENDER_MAX_STACKS(       new Entry<>(4)),
      WINGED_MAX_STACKS(      new Entry<>(4)),
      METAL_MAX_STACKS(       new Entry<>(7)),
      POT_MAX_STACKS(         new Entry<>(128)),
      CAULDRON_MAX_BUCKETS(   new Entry<>(24)),
      UNBIND_ENDER_ON_DEATH(  new Entry<>(false)),
      LOCK_ENDER_OFFLINE(     new Entry<>(false)),

      // Client Config
      MENU_VISIBILITY         (new Entry<>(MenuVisibility.SHOWN));

      public static final int[] MAX_STACKS_RANGE = {1, 64};
      public static final int[] MAX_BUCKETS_RANGE = {1, 128};

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
