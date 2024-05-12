package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Iterator;

public class ItemListConfigVariant extends HSetConfigVariant<Item> {
      private HashSet<ResourceLocation> heldLocations;
      private ItemListConfigVariant(String name, HashSet<Item> set, HashSet<ResourceLocation> heldLocations) {
            super(name, set, null, null);
            this.heldLocations = heldLocations;
      }

      public static ItemListConfigVariant create(String name, String... items) {
            HashSet<Item> hSet = new HashSet<>();
            HashSet<ResourceLocation> heldLocations = new HashSet<>();
            for (String key : items) {
                  ResourceLocation location = new ResourceLocation(key);
                  Item item = BuiltInRegistries.ITEM.get(location);
                  if (!item.asItem().equals(Items.AIR))
                        hSet.add(item);
                  else if (!location.getNamespace().equals("minecraft"))
                        heldLocations.add(location);
            }
            return new ItemListConfigVariant(name, hSet, heldLocations);
      }

      @Override
      public String encode() {
            StringBuilder sb = new StringBuilder().append(toString());
            sb.append('"');

            Iterator<Item> iterator = value.iterator();
            while (iterator.hasNext()) {
                  String name = BuiltInRegistries.ITEM.getKey(iterator.next()).toShortLanguageKey();
                  sb.append(name);
                  if (iterator.hasNext())
                        sb.append(", ");
            }

            Iterator<ResourceLocation> locations = heldLocations.iterator();
            while (locations.hasNext()) {
                  sb.append(locations.next());
                  if (locations.hasNext())
                        sb.append(", ");
            }

            sb.append('"');
            return sb.toString();
      }

      @Override
      public void decode(JsonObject jsonObject) {
            if (!jsonObject.has(name)) return;

            String string = GsonHelper.getAsString(jsonObject, name, "");
            if (Constants.isEmpty(string)) return;

            String[] split = string.replace(" ", "").split(",");
            for (String entry : split) {
                  ResourceLocation location = new ResourceLocation(entry);
                  Item item = BuiltInRegistries.ITEM.get(location);
                  if (!item.asItem().equals(Items.AIR))
                        value.add(item);
                  else if (!location.getNamespace().equals("minecraft"))
                        heldLocations.add(location);
            }
      }
}
