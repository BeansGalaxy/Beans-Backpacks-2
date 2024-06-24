package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;

public class ConfigLabel implements ConfigLine {
      private final String label;

      public ConfigLabel(String label) {
            this.label = label;
      }

      @Override
      public String encode() {
            int hLineWidth = Math.max(0, 44 - label.length());
            int hLineFloor = hLineWidth / 2;
            int hLineCeil = hLineFloor + hLineWidth % 2;
            return "/* " + "=".repeat(hLineFloor) + ' ' + label + ' ' + "=".repeat(hLineCeil) + " */";
      }

      @Override
      public String toString() {
            return label;
      }

      @Override
      public String comment(int max) {
            return "";
      }

      @Override
      public void decode(JsonObject jsonObject) {
      }

      @Override
      public boolean punctuate() {
            return false;
      }
}
