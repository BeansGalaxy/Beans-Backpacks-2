package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class HMapConfigVariant<KEY, ENTRY> extends ConfigVariant<HashMap<KEY, ENTRY>> {
      private final Function<KEY, String> keyEncode;
      private final Function<String, KEY> keyDecode;
      private final UnaryOperator<ENTRY> validate;
      private final Function<ENTRY, String> entryEncode;
      private final Function<String, ENTRY> entryDecode;
      private final HashMap<KEY, ENTRY> example;

      private HMapConfigVariant(String name, HashMap<KEY, ENTRY> defau, UnaryOperator<ENTRY> validate, Function<KEY, String> keyEncode, Function<String, KEY> keyDecode, Function<ENTRY, String> entryEncode, Function<String, ENTRY> entryDecode, HashMap<KEY, ENTRY> example, String comment) {
            super(name, defau, comment);
            this.keyEncode = keyEncode;
            this.keyDecode = keyDecode;
            this.validate = validate;
            this.entryEncode = entryEncode;
            this.entryDecode = entryDecode;
            this.example = example;
      }

      @Override
      public String comment(int whiteSpace) {
            return "";
      }

      @Override
      public String encode() {
            String formattedName = toString();
            StringBuilder sb = new StringBuilder().append(formattedName);
            sb.append("{");
            if (!comment.isBlank())
                  sb.append(" ".repeat(Math.max(0, 34 - formattedName.length())))
                              .append("// ").append(comment);
            writeValues(sb);
            writeExamples(sb);
            sb.append("\n  }");
            return sb.toString();
      }

      private void writeValues(StringBuilder sb) {
            HashMap<KEY, ENTRY> map = value;
            Iterator<KEY> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                  KEY key = iterator.next();
                  ENTRY entry = validate.apply(map.get(key));
                  sb.append("\n    \"");
                  sb.append(keyEncode.apply(key)).append("\": ").append(entryEncode.apply(entry));
                  if (iterator.hasNext())
                        sb.append(",");
            }
      }

      private void writeExamples(StringBuilder sb) {
            HashMap<KEY, ENTRY> map = new HashMap<>(example);
            for (KEY key : value.keySet())
                  map.remove(key);

            Iterator<KEY> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                  KEY key = iterator.next();
                  ENTRY entry = validate.apply(map.get(key));
                  sb.append("\n    //\"");
                  sb.append(keyEncode.apply(key)).append("\": ").append(entryEncode.apply(entry));
                  if (iterator.hasNext())
                        sb.append(",");
            }
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            JsonObject jsonValue = jsonObject.getAsJsonObject(name);
            for (String key : jsonValue.keySet()) {
                  String entry = GsonHelper.getAsString(jsonValue, key);
                  value.put(keyDecode.apply(key), validate.apply(entryDecode.apply(entry)));
            }
      }

      public static class Builder<K, E> {
            private final HashMap<K, E> defau = new HashMap<>();
            private final HashMap<K, E> example = new HashMap<>();
            private final Function<K, String> keyEncode;
            private final Function<String, K> keyDecode;
            private final Function<E, String> entryEncode;
            private final Function<String, E> entryDecode;
            private UnaryOperator<E> validator = in -> in;
            private String comment = "";

            private Builder(Function<K, String> keyEncode, Function<String, K> keyDecode, Function<E, String> entryEncode, Function<String, E> entryDecode) {
                  this.keyEncode = keyEncode;
                  this.keyDecode = keyDecode;
                  this.entryEncode = entryEncode;
                  this.entryDecode = entryDecode;
            }

            public static <K, E> Builder<K, E> create(Function<K, String> keyEncode, Function<String, K> keyDecode, Function<E, String> entryEncode, Function<String, E> entryDecode) {
                  return new Builder<>(keyEncode, keyDecode, entryEncode, entryDecode);
            }

            public static <E> Builder<String, E> create(Function<E, String> entryEncode, Function<String, E> entryDecode) {
                  Function<String, String> transcode = in -> in;
                  return new Builder<>(transcode, transcode, entryEncode, entryDecode);
            }

            public Builder<K, E> defau(K[] keys, E[] entries) {
                  int size = Math.min(keys.length, entries.length);
                  for (int i = 0; i < size; i++)
                        defau.put(keys[i], entries[i]);
                  return this;
            }

            public Builder<K, E> example(K[] keys, E[] entries) {
                  int size = Math.min(keys.length, entries.length);
                  for (int i = 0; i < size; i++)
                        example.put(keys[i], entries[i]);
                  return this;
            }

            public Builder<K, E> validator(UnaryOperator<E> validate) {
                  this.validator = validate;
                  return this;
            }

            public Builder<K, E> comment(String comment) {
                  this.comment = comment;
                  return this;
            }

            public HMapConfigVariant<K, E> build(String name) {
                  return new HMapConfigVariant<>(name, defau, validator, keyEncode, keyDecode, entryEncode, entryDecode, example, comment);
            }
      }
}
