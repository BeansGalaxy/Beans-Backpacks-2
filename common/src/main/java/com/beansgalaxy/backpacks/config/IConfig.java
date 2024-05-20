package com.beansgalaxy.backpacks.config;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.types.ConfigLine;
import com.beansgalaxy.backpacks.config.types.ConfigLabel;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.platform.Services;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public interface IConfig {

      String getPath();

      Collection<ConfigLine> getLines();

      default void parse(String encodedConfig) {
            // Parse the JSON content into a Person object
            JsonObject jsonObject = JsonParser.parseString(encodedConfig).getAsJsonObject();
            Collection<ConfigLine> lines = getLines();
            for (ConfigLine line : lines) {
                  line.decode(jsonObject);
            }
      }
      default void read() {
            read(true);
      }

      default void read(boolean andWrite) {
            try {
                  // Read the .json5 content from the file
                  Path path = Services.CONFIG.getPath();
                  Path resolve = path.resolve(Constants.MOD_ID + getPath() + ".json5");
                  String json5Content = new String(Files.readAllBytes(resolve));

                  // Remove comments from the .json5 content
                  String jsonContent = json5Content.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "");
                  parse(jsonContent);

            } catch (IOException e) {
                  Constants.LOG.warn("No Config for " + Constants.MOD_ID + " : Created new config");
            }

            if (andWrite) this.write();
      }

      default void write() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");

            Iterator<ConfigLine> iterator = getLines().iterator();
            while (iterator.hasNext()) {
                  ConfigLine line = iterator.next();
                  String encode = line.encode();
                  if (line.punctuate())
                        sb.append("  ");
                  sb.append(encode);
                  int length = encode.length();
                  if (line.punctuate() && iterator.hasNext()) {
                        sb.append(',');
                        length += 1;
                  }

                  sb.append(line.comment(Math.max(0, 34 - length)));
                  sb.append('\n');
            }
            sb.append('}');

            try {
                  Path path = Services.CONFIG.getPath();
                  Path resolve = path.resolve(Constants.MOD_ID + getPath() + ".json5");
                  String string = sb.toString();
                  Files.writeString(resolve, string);
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      /* ============================= Item Whitelist Helpers ============================ */

      static boolean chestplateDisabled(Item item) {
            return ServerSave.CONFIG.disable_chestplate.get().contains(item);
      }

      static boolean disablesBackSlot(Item item) {
            return ServerSave.CONFIG.disables_back_slot.get().contains(item);
      }

      static boolean blacklistedItem(Item item) {
            return ServerSave.CONFIG.blacklist_items.get().contains(item);
      }

      static boolean elytraItem(Item item) {
            return ServerSave.CONFIG.elytra_items.get().contains(item);
      }

      static boolean elytraOrDisables(Item item) {
            return !item.equals(Items.AIR) && (disablesBackSlot(item) || elytraItem(item));
      }

      static boolean cantEquipWithBackpack(Item item) {
            return !item.equals(Items.AIR) && (disablesBackSlot(item) || chestplateDisabled(item));
      }
}
