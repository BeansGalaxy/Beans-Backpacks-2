package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;

public interface ConfigLine {
      String encode();

      String comment();

      void decode(JsonObject jsonObject);
}
