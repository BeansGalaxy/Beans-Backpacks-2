package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.Iterator;

public class EnumConfigVariant<T extends Enum<T>> extends ConfigVariant<T> {
      private final T[] values;

      public EnumConfigVariant(String name, T defau, T[] values) {
            super(name, defau, "");
            this.values = values;
      }

      public EnumConfigVariant(String name, T defau, T[] values, String comment) {
            super(name, defau, comment);
            this.values = values;
      }

      @Override
      public String autoComment() {
            StringBuilder sb = new StringBuilder();
            Iterator<T> iterator = Arrays.stream(values).iterator();
            while (iterator.hasNext()) {
                  T next = iterator.next();
                  sb.append(next);
                  if (next == getDefau())
                        sb.append(" (Default)");
                  if(iterator.hasNext())
                        sb.append(" : ");
            }

            return sb.toString();
      }

      @Override
      public String encode() {
            return toString() + '"' + value.name() + '"';
      }

      @Override
      public void decode(JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, name);
            for (T value : values) {
                  if (value.name().equals(string))
                        this.value = value;
            }
      }
}
