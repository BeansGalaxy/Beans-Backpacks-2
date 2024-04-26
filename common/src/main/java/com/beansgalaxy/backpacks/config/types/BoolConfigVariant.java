package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class BoolConfigVariant extends ConfigVariant<Boolean> {

      public BoolConfigVariant(String name, boolean defau) {
            super(name, defau, "");
      }

      public BoolConfigVariant(String name, boolean defau, String comment) {
            super(name, defau, comment);
      }

      @Override
      public String autoComment() {
            return "Default: " + defau;
      }

      @Override
      public String encode() {
            return formattedName() + value;
      }

      @Override
      public void decode(JsonObject jsonObject) {
            value = GsonHelper.getAsBoolean(jsonObject, name, defau);
      }

}
