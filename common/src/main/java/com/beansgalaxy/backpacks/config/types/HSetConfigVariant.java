package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class HSetConfigVariant<ENTRY> extends ConfigVariant<HashSet<ENTRY>> {
      private final Function<ENTRY, String> encode;
      private final Function<String, ENTRY> decode;
      private final Predicate<String> isValid;
      private final HashSet<String> rejects;

      private HSetConfigVariant(String name, HashSet<ENTRY> defau, HashSet<String> rejects, Predicate<String> isValid, Function<ENTRY, String> encode, Function<String, ENTRY> decode, String comment) {
            super(name, defau, comment);
            this.encode = encode;
            this.decode = decode;
            this.isValid = isValid;
            this.rejects = rejects;
      }

      @Override
      public String encode() {
            StringBuilder sb = new StringBuilder().append(this);
            sb.append('"');
            Iterator<ENTRY> iterator = value.iterator();
            Iterator<String> rejected = rejects.iterator();
            while (rejected.hasNext()) {
                  String entry = rejected.next();
                  sb.append(entry);
                  if (iterator.hasNext() || rejected.hasNext())
                        sb.append(", ");
            }

            while (iterator.hasNext()) {
                  String entry = encode.apply(iterator.next());
                  sb.append(entry);
                  if (iterator.hasNext())
                        sb.append(", ");
            }
            sb.append('"');

            return sb.toString();
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            value.clear();
            rejects.clear();
            String string = GsonHelper.getAsString(jsonObject, name);
            decode(string, isValid, decode, value, rejects);
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
            private final Function<String, E> decode;
            private HashSet<E> defau = new HashSet<>();
            private String defauString = "";
            private Predicate<String> isValid = in -> true;
            private String comment = "";

            private Builder(Function<E, String> encode, Function<String, E> decode) {
                  this.encode = encode;
                  this.decode = decode;
            }

            public static <E> Builder<E> create(Function<E, String> encode, Function<String, E> decode) {
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

            public Builder<E> defauString(String defau) {
                  this.defauString = defau;
                  return this;
            }

            /**
             * Used while indexing through the list written in the json file. Passes each entry of the list through before converting
             * it to ENTRY to check if it is valid to be converted.
             * @param validate If false, the list's entry will; be saved back to config, not be converted, and not be used.
             */
            public Builder<E> isValid(Predicate<String> validate) {
                  this.isValid = validate;
                  return this;
            }

            public HSetConfigVariant<E> build(String name) {
                  HashSet<String> rejects = new HashSet<>();
                  HSetConfigVariant.decode(defauString, isValid, decode, defau, rejects);
                  return new HSetConfigVariant<>(name, defau, rejects, isValid, encode, decode, comment);
            }
      }
}
