package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;

public class ConfigComment implements ConfigLine {
      private final String comment;

      public ConfigComment(String comment) {
            this.comment = comment;
      }

      @Override
      public String encode() {
            return "   // " + comment;
      }

      @Override
      public String toString() {
            return comment;
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
