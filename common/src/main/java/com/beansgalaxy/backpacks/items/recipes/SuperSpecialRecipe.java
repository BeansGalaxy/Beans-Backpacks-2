package com.beansgalaxy.backpacks.items.recipes;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SuperSpecialRecipe extends CustomRecipe {
      public static final String ID = "super_special_players";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<SuperSpecialRecipe> INSTANCE = new SuperSpecialRecipe.Serializer();
      private final ItemStack result;
      private final NonNullList<Ingredient> ingredients;

      public SuperSpecialRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> ingredients) {
            super(id, CraftingBookCategory.EQUIPMENT);
            this.result = result;
            this.ingredients = ingredients;
      }

      public boolean matches(CraftingContainer $$0, Level $$1) {
            StackedContents $$2 = new StackedContents();
            int $$3 = 0;

            for(int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
                  ItemStack $$5 = $$0.getItem($$4);
                  if (!$$5.isEmpty()) {
                        ++$$3;
                        $$2.accountStack($$5, 1);
                  }
            }

            return $$3 == this.ingredients.size() && $$2.canCraft(this, null);
      }

      public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
            return this.result.copy();
      }

      @Override
      public boolean canCraftInDimensions(int i, int i1) {
            return true;
      }

      @Override
      public boolean isIncomplete() {
            return super.isIncomplete();
      }

      @Override @NotNull
      public ItemStack getResultItem(RegistryAccess registryAccess) {
            return result;
      }

      @Override
      public NonNullList<Ingredient> getIngredients() {
            return ingredients;
      }

      @Override @NotNull
      public RecipeSerializer<?> getSerializer() {
            return INSTANCE;
      }

      public boolean isSuperSpecialPlayer(Player player) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                  ServerSave save = ServerSave.getSave(server, false);
                  return save.isSuperSpecial(player);
            }
            return false;
      }

      public static class Serializer implements RecipeSerializer<SuperSpecialRecipe> {

            @Override @NotNull
            public SuperSpecialRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
                  NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
                  if (ingredients.isEmpty()) {
                        throw new JsonParseException("No ingredients for shapeless recipe");
                  } else if (ingredients.size() > 9) {
                        throw new JsonParseException("Too many ingredients for shapeless recipe");
                  } else {
                        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                        return new SuperSpecialRecipe(id, result, ingredients);
                  }
            }

            private NonNullList<Ingredient> itemsFromJson(JsonArray ingredients) {
                  NonNullList<Ingredient> $$1 = NonNullList.create();
                  for (JsonElement ingredient : ingredients) {
                        Ingredient $$3 = Ingredient.fromJson(ingredient, false);
                        if (!$$3.isEmpty()) {
                              $$1.add($$3);
                        }
                  }

                  return $$1;
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, SuperSpecialRecipe recipe) {
                  buf.writeVarInt(recipe.ingredients.size());
                  for (Ingredient $$2 : recipe.ingredients) {
                        $$2.toNetwork(buf);
                  }

                  buf.writeItem(recipe.result);
            }

            @Override @NotNull
            public SuperSpecialRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                  int $$4 = buf.readVarInt();
                  NonNullList<Ingredient> ingredients = NonNullList.withSize($$4, Ingredient.EMPTY);

                  for(int $$6 = 0; $$6 < ingredients.size(); ++$$6) {
                        ingredients.set($$6, Ingredient.fromNetwork(buf));
                  }

                  ItemStack result = buf.readItem();
                  return new SuperSpecialRecipe(id, result, ingredients);
            }
      }
}
