package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListConfigVariant<ENTRY> extends ConfigVariant<ArrayList<ENTRY>> {
      private final Function<ENTRY, String> encode;
      private final Function<JsonElement, ENTRY> decode;
      private final Predicate<ArrayList<ENTRY>> validate;

      private ListConfigVariant(String name, ArrayList<ENTRY> defau, Predicate<ArrayList<ENTRY>> validate, Function<ENTRY, String> encode, Function<JsonElement, ENTRY> decode, String comment) {
            super(name, defau, comment);
            this.value = new ArrayList<>(defau);
            this.validate = validate;
            this.encode = encode;
            this.decode = decode;
      }

      public ENTRY get(int i) {
            return value.get(i);
      }

      @Override
      public String encode() {
            if (!validate.test(value)) value = defau;

            StringBuilder sb = new StringBuilder().append(this);
            sb.append('[');

            Iterator<ENTRY> iterator = value.iterator();
            while (iterator.hasNext()) {
                  String entry = encode.apply(iterator.next());
                  sb.append(entry);
                  if (iterator.hasNext())
                        sb.append(", ");
            }
            sb.append(']');

            return sb.toString();
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            value.clear();
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, name);
            for (int i = 0; i < jsonArray.size(); i++) {
                  JsonElement jsonElement = jsonArray.get(i);
                  value.add(i, decode.apply(jsonElement));
            }
      }

      private static <E> void decode(String encoded, Predicate<String> isValid, Function<String, E> decode, HashSet<E> value, HashSet<String> rejects) {
            String[] split = encoded.replace(" ", "").split(",");
            for (String entry : split) {
                  if (Constants.isEmpty(entry))
                        continue;
                  if (isValid.test(entry)) {
                        E apply = decode.apply(entry);
                        value.add(apply);
                  }
                  else rejects.add(entry);
            }
      }

      public static class Builder<E> {
            private final Function<E, String> encode;
            private final Function<JsonElement, E> decode;
            private ArrayList<E> defau = new ArrayList<>();
            private Predicate<ArrayList<E>> valid = in -> true;
            private String comment = "";

            private Builder(Function<E, String> encode, Function<JsonElement, E> decode) {
                  this.encode = encode;
                  this.decode = decode;
            }

            public static <E> Builder<E> create(Function<E, String> encode, Function<JsonElement, E> decode) {
                  return new Builder<>(encode, decode);
            }

            public Builder<E> comment(String comment) {
                  this.comment = comment;
                  return this;
            }

            public Builder<E> defau(E... defau) {
                  this.defau.addAll(Arrays.asList(defau));
                  return this;
            }

            public Builder<E> valid(Predicate<ArrayList<E>> valid) {
                  this.valid = valid;
                  return this;
            }

            public ListConfigVariant<E> build(String name) {
                  return new ListConfigVariant<>(name, defau, valid, encode, decode, comment);
            }
      }
}
