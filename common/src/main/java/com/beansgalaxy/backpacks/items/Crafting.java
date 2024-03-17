package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Crafting extends CustomRecipe {
      public static final String ID = "crafting";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<Crafting> INSTANCE = new Crafting.Serializer();
      private final String backpack_id;
      private final NonNullList<Ingredient> ingredients;

      public Crafting(ResourceLocation id, String backpack_id, NonNullList<Ingredient> ingredients) {
            super(id, CraftingBookCategory.EQUIPMENT);
            this.backpack_id = backpack_id;
            this.ingredients = ingredients;
      }

      @Override
      public boolean isSpecial() {
            return false;
      }

      @Override
      public boolean matches(CraftingContainer container, Level level) {
            if (container.getWidth() != 3 || container.getHeight() != 3 || backpack_id == null || backpack_id.isEmpty())
                  return false;

            Traits traits = Traits.get(backpack_id);
            if (traits.isEmpty())
                  return false;

            if (ingredients.isEmpty())
                  return false;

            int i = 0;
            for (Ingredient ingredient : ingredients) {
                  if (i >= container.getContainerSize())
                        return false;

                  ItemStack item = container.getItem(i);
                  if (!ingredient.test(item))
                        return false;
                  i++;
            }

            return true;
      }

      @Override @NotNull
      public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
            return getResultItem(registryAccess);
      }

      @Override
      public boolean canCraftInDimensions(int i, int i1) {
            return true;
      }

      @Override
      public boolean isIncomplete() {
            return backpack_id == null || backpack_id.isEmpty();
      }

      @Override @NotNull
      public ItemStack getResultItem(RegistryAccess registryAccess) {
            return Traits.toStack(backpack_id);
      }

      @Override @NotNull
      public NonNullList<Ingredient> getIngredients() {
            return ingredients;
      }

      @Override
      public @NotNull RecipeSerializer<Crafting> getSerializer() {
            return INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<Crafting> {

            @Override @NotNull
            public Crafting fromJson(ResourceLocation id, JsonObject jsonObject) {
                  String key = GsonHelper.getAsString(jsonObject, "backpack_id", "");
                  Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
                  String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
                  int $$6 = astring[0].length();
                  int $$7 = astring.length;
                  NonNullList<Ingredient> ingredients = dissolvePattern(astring, map, $$6, $$7);
                  return new Crafting(id, key, ingredients);
            }

            static NonNullList<Ingredient> dissolvePattern(String[] $$0, Map<String, Ingredient> $$1, int $$2, int $$3) {
                  NonNullList<Ingredient> $$4 = NonNullList.withSize($$2 * $$3, Ingredient.EMPTY);
                  Set<String> $$5 = Sets.newHashSet($$1.keySet());
                  $$5.remove(" ");

                  for(int $$6 = 0; $$6 < $$0.length; ++$$6) {
                        for(int $$7 = 0; $$7 < $$0[$$6].length(); ++$$7) {
                              String $$8 = $$0[$$6].substring($$7, $$7 + 1);
                              Ingredient $$9 = (Ingredient)$$1.get($$8);
                              if ($$9 == null) {
                                    throw new JsonSyntaxException("Pattern references symbol '" + $$8 + "' but it's not defined in the key");
                              }

                              $$5.remove($$8);
                              $$4.set($$7 + $$2 * $$6, $$9);
                        }
                  }

                  if (!$$5.isEmpty()) {
                        throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + $$5);
                  } else {
                        return $$4;
                  }
            }

            private static int firstNonSpace(String $$0) {
                  int $$1;
                  for($$1 = 0; $$1 < $$0.length() && $$0.charAt($$1) == ' '; ++$$1) {
                  }

                  return $$1;
            }

            private static int lastNonSpace(String $$0) {
                  int $$1;
                  for($$1 = $$0.length() - 1; $$1 >= 0 && $$0.charAt($$1) == ' '; --$$1) {
                  }

                  return $$1;
            }

            @VisibleForTesting
            static String[] shrink(String... $$0) {
                  int $$1 = Integer.MAX_VALUE;
                  int $$2 = 0;
                  int $$3 = 0;
                  int $$4 = 0;

                  for(int $$5 = 0; $$5 < $$0.length; ++$$5) {
                        String $$6 = $$0[$$5];
                        $$1 = Math.min($$1, firstNonSpace($$6));
                        int $$7 = lastNonSpace($$6);
                        $$2 = Math.max($$2, $$7);
                        if ($$7 < 0) {
                              if ($$3 == $$5) {
                                    ++$$3;
                              }

                              ++$$4;
                        } else {
                              $$4 = 0;
                        }
                  }

                  if ($$0.length == $$4) {
                        return new String[0];
                  } else {
                        String[] $$8 = new String[$$0.length - $$4 - $$3];

                        for(int $$9 = 0; $$9 < $$8.length; ++$$9) {
                              $$8[$$9] = $$0[$$9 + $$3].substring($$1, $$2 + 1);
                        }

                        return $$8;
                  }
            }

            static String[] patternFromJson(JsonArray $$0) {
                  String[] $$1 = new String[$$0.size()];
                  if ($$1.length > 3) {
                        throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
                  } else if ($$1.length == 0) {
                        throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
                  } else {
                        for(int $$2 = 0; $$2 < $$1.length; ++$$2) {
                              String $$3 = GsonHelper.convertToString($$0.get($$2), "pattern[" + $$2 + "]");
                              if ($$3.length() > 3) {
                                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                              }

                              if ($$2 > 0 && $$1[0].length() != $$3.length()) {
                                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                              }

                              $$1[$$2] = $$3;
                        }

                        return $$1;
                  }
            }

            static Map<String, Ingredient> keyFromJson(JsonObject $$0) {
                  Map<String, Ingredient> $$1 = Maps.newHashMap();
                  Iterator var2 = $$0.entrySet().iterator();

                  while(var2.hasNext()) {
                        Map.Entry<String, JsonElement> $$2 = (Map.Entry)var2.next();
                        if (((String)$$2.getKey()).length() != 1) {
                              throw new JsonSyntaxException("Invalid key entry: '" + (String)$$2.getKey() + "' is an invalid symbol (must be 1 character only).");
                        }

                        if (" ".equals($$2.getKey())) {
                              throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                        }

                        $$1.put((String)$$2.getKey(), Ingredient.fromJson((JsonElement)$$2.getValue(), false));
                  }

                  $$1.put(" ", Ingredient.EMPTY);
                  return $$1;
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, Crafting recipe) {
                  buf.writeUtf(recipe.backpack_id);
                  buf.writeInt(recipe.ingredients.size());
                  for (Ingredient ingredient : recipe.ingredients)
                        ingredient.toNetwork(buf);
            }

            @Override @NotNull
            public Crafting fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                  String key = buf.readUtf();
                  int size = buf.readInt();
                  NonNullList<Ingredient> ingredients = NonNullList.create();
                  for (int i = 0; i < size; i++) {
                        Ingredient ingredient = Ingredient.fromNetwork(buf);
                        ingredients.add(ingredient);
                  }

                  return new Crafting(id, key, ingredients);
            }
      }
}
