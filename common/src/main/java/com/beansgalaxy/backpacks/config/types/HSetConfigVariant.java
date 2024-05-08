package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;

public class HSetConfigVariant<ENTRY> extends ConfigVariant<HashSet<ENTRY>> {
      private final Function<ENTRY, String> encode;
      private final Function<String, ENTRY> decode;

      public HSetConfigVariant(String name, HashSet<ENTRY> set, Function<ENTRY, String> encode, Function<String, ENTRY> decode) {
            super(name, set, "");
            this.encode = encode;
            this.decode = decode;
      }

      public HSetConfigVariant(String name, HashSet<ENTRY> set, Function<ENTRY, String> encode, Function<String, ENTRY> decode, String comment) {
            super(name, set, comment);
            this.encode = encode;
            this.decode = decode;
      }

      @Override
      public String encode() {
            StringBuilder sb = new StringBuilder().append(formattedName());
            sb.append('"');
            Iterator<ENTRY> iterator = value.iterator();
            while (iterator.hasNext()) {
                  String name = encode.apply(iterator.next());
                  sb.append(name);
                  if (iterator.hasNext())
                        sb.append(", ");
            }
            sb.append('"');

            return sb.toString();
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            String string = GsonHelper.getAsString(jsonObject, name);
            String[] split = string.replace(" ", "").split(",");
            for (String entry : split) {
                  if (Constants.isEmpty(entry))
                        continue;
                  ENTRY apply = decode.apply(entry);
                  value.add(apply);
            }
      }
}
