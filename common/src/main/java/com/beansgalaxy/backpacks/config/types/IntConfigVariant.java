package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class IntConfigVariant extends ConfigVariant<Integer> {
      private final int max;
      private final int min;
      private final boolean clamped;

      public IntConfigVariant(String name, Integer defau) {
            super(name, defau, "");
            this.value = defau;
            this.clamped = false;
            this.max = Integer.MAX_VALUE;
            this.min = Integer.MIN_VALUE;
      }

      public IntConfigVariant(String name, int defau, int min, int max) {
            super(name, defau, "");
            this.value = defau;
            this.clamped = true;
            this.max = max;
            this.min = min;
      }

      public IntConfigVariant(String name, Integer defau, String comment) {
            super(name, defau, comment);
            this.value = defau;
            this.clamped = false;
            this.max = Integer.MAX_VALUE;
            this.min = Integer.MIN_VALUE;
      }

      public IntConfigVariant(String name, int defau, int min, int max, String comment) {
            super(name, defau, comment);
            this.value = defau;
            this.clamped = true;
            this.max = max;
            this.min = min;
      }

      @Override
      public String autoComment() {
            StringBuilder sb = new StringBuilder();
            if (clamped)
                  sb.append(min).append(" - ").append(max).append("  ");
            sb.append("Default: ").append(defau);
            return sb.toString();
      }

      @Override
      public String encode() {
            return formattedName() + value;
      }

      @Override
      public void decode(JsonObject jsonObject) {
            int i = GsonHelper.getAsInt(jsonObject, name, defau);
            value = clamped
                  ? Mth.clamp(i, min, max)
                  : i;
      }
}
