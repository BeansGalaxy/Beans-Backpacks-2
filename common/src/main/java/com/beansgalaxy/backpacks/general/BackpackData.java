package com.beansgalaxy.backpacks.general;

public class BackpackData {
      private String key;
      private String name;
      private String color;
      private String trim_material;
      private String trim_pattern;

      public BackpackData(String name) {
            this.key = name;
      }

      public String getKey() {
            return key;
      }

      public String getName() {
            return name;
      }

      public void setKey(String key) {
            this.key = key;
      }

      public void setName(String name) {
            this.name = name;
      }
}
