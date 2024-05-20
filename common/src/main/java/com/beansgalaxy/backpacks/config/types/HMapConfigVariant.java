package com.beansgalaxy.backpacks.config.types;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class HMapConfigVariant<KEY, ENTRY> extends ConfigVariant<HashMap<KEY, ENTRY>> {
      private final Function<KEY, String> keyEncode;
      private final Function<String, KEY> keyDecode;
      private final BiPredicate<String, ENTRY> injection;
      private final HashMap<String, ENTRY> injectedMap = new HashMap<>();
      private final UnaryOperator<ENTRY> validate;
      private final Function<ENTRY, String> entryEncode;
      private final Function<String, ENTRY> entryDecode;
      private final HashMap<String, ENTRY> example;

      private HMapConfigVariant(String name, HashMap<KEY, ENTRY> defau, BiPredicate<String, ENTRY> injection, UnaryOperator<ENTRY> validate, Function<KEY, String> keyEncode, Function<String, KEY> keyDecode, Function<ENTRY, String> entryEncode, Function<String, ENTRY> entryDecode, HashMap<String, ENTRY> example, String comment) {
            super(name, defau, comment);
            this.value = new HashMap<>(defau);
            this.keyEncode = keyEncode;
            this.keyDecode = keyDecode;
            this.injection = injection;
            this.validate = validate;
            this.entryEncode = entryEncode;
            this.entryDecode = entryDecode;
            this.example = example;
      }

      public ENTRY get(KEY key) {
            return value.get(key);
      }

      public boolean contains(KEY key) {
            return value.containsKey(key);
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
            Iterator<String> injectIterator = injectedMap.keySet().iterator();
            while (injectIterator.hasNext()) {
                  String key = injectIterator.next();
                  ENTRY entry = validate.apply(injectedMap.get(key));
                  sb.append("\n    \"");
                  sb.append(key).append("\": ").append(entryEncode.apply(entry));
                  if (iterator.hasNext() || injectIterator.hasNext())
                        sb.append(",");
            }

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
            HashMap<String, ENTRY> map = new HashMap<>(example);
            for (KEY key : value.keySet()) {
                  String apply = keyEncode.apply(key);
                  map.remove(apply);
            }

            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                  String key = iterator.next();
                  ENTRY entry = validate.apply(map.get(key));
                  sb.append("\n    //\"");
                  sb.append(key).append("\": ").append(entryEncode.apply(entry));
                  if (iterator.hasNext())
                        sb.append(",");
            }
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            value.clear();
            JsonObject jsonValue = jsonObject.getAsJsonObject(name);
            for (String key : jsonValue.keySet()) {
                  String entry = GsonHelper.getAsString(jsonValue, key);
                  ENTRY appliedEntry = entryDecode.apply(entry);
                  if (injection.test(key, appliedEntry)) {
                        injectedMap.put(key, appliedEntry);
                        continue;
                  }

                  KEY appliedKey = keyDecode.apply(key);
                  if (appliedKey == null) continue;

                  value.put(appliedKey, validate.apply(appliedEntry));
            }
      }

      public static class Builder<K, E> {
            private final HashMap<K, E> defau = new HashMap<>();
            private final HashMap<String, E> example = new HashMap<>();
            private final Function<K, String> keyEncode;
            private final Function<String, K> keyDecode;
            private final Function<E, String> entryEncode;
            private final Function<String, E> entryDecode;
            private BiPredicate<String, E> injection = (k, e) -> false;
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

            public Builder<K, E> example(String[] keys, E[] entries) {
                  int size = Math.min(keys.length, entries.length);
                  for (int i = 0; i < size; i++)
                        example.put(keys[i], entries[i]);
                  return this;
            }

            /**
             * Runs this while collecting every key in the json
             * @param injection Continues to the next entry if returns true
             */
            public Builder<K, E> inject(BiPredicate<String, E> injection) {
                  this.injection = injection;
                  return this;
            }

            public Builder<K, E> validEntry(UnaryOperator<E> validate) {
                  this.validator = validate;
                  return this;
            }

            public Builder<K, E> comment(String comment) {
                  this.comment = comment;
                  return this;
            }

            public HMapConfigVariant<K, E> build(String name) {
                  return new HMapConfigVariant<>(name, defau, injection, validator, keyEncode, keyDecode, entryEncode, entryDecode, example, comment);
            }
      }
}
