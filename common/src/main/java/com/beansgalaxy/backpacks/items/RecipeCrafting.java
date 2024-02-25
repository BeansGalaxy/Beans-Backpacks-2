package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.Traits;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RecipeCrafting extends CustomRecipe {
      public static final String ID = "backpack_crafting";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<RecipeCrafting> INSTANCE = new Serializer();
      private final String key;

      public RecipeCrafting(ResourceLocation id, String key) {
            super(id, CraftingBookCategory.EQUIPMENT);
            this.key = key;
      }

      @Override
      public boolean isSpecial() {
            return false;
      }

      @Override
      public String getGroup() {
            return Constants.MOD_ID + "_crafting";
      }

      @Override
      public boolean matches(CraftingContainer container, Level level) {
            if (container.getWidth() != 3 || container.getHeight() != 3)
                  return false;

            Item material = container.getItem(0).getItem();
            Item binder = container.getItem(1).getItem();

            if (container.getItem(0).isEmpty() || container.getItem(1).isEmpty())
                  return false;

            if (binder != container.getItem(7).getItem())
                  return false;

            int[] materialSlots = {2, 3, 5, 6, 8};
            for (int i: materialSlots)
            {
                  Item item = container.getItem(i).getItem();
                  if (item != material)
                        return false;
            }

//          RETURNS TRUE ONLY IF...
//           - CRAFTING GRID IS 3x3
//           - MATERIALS AND BINDERS ARE IN THEIR CORRECT SLOTS
//           - THERE IS A REGISTERED BACKPACK WITH THOSE MATERIALS AND BINDERS

            String key = Traits.keyFromIngredients(material, binder);
            return key != null && !key.isEmpty();
      }

      @Override
      public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess level) {
            String key = Traits.keyFromIngredients(container.getItem(0).getItem(), container.getItem(1).getItem());
            if (key == null || key.isEmpty())
                  return ItemStack.EMPTY;

            return BackpackItem.stackFromKey(key);
      }

      @Override
      public boolean canCraftInDimensions(int $$0, int $$1) {
            return true;
      }

      @Override
      public boolean isIncomplete() {
            NonNullList<Ingredient> $$0 = this.getIngredients();
            boolean incomplete = $$0.isEmpty() || $$0.stream().filter(($$0x) -> {
                  return !$$0x.isEmpty();
            }).anyMatch(($$0x) -> {
                  return $$0x.getItems().length == 0;
            });
            return incomplete;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess $$0) {
            return getResultItem(this.key);
      }

      public static ItemStack getResultItem(String key) {
            if (key == null || key.isEmpty())
                  return ItemStack.EMPTY;
            return BackpackItem.stackFromKey(key);
      }

      @Override
      public NonNullList<Ingredient> getIngredients() {
            return getIngredients(this.key);
      }

      public static NonNullList<Ingredient> getIngredients(String key) {

            Traits traits = Traits.get(key);
            Ingredient mat = Ingredient.of(traits.material);
            Ingredient bin = Ingredient.of(traits.binder);
            Ingredient emp = Ingredient.EMPTY;

            return NonNullList.of(Ingredient.EMPTY,
                        mat, bin, mat,
                        mat, emp, mat,
                        mat, bin, mat);
      }

      @Override
      public @NotNull RecipeSerializer<RecipeCrafting> getSerializer() {
            return INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<RecipeCrafting> {

            @Override
            public RecipeCrafting fromJson(ResourceLocation id, JsonObject jsonObject) {
                  String key = GsonHelper.getAsString(jsonObject, "key", "");
                  return new RecipeCrafting(id, key);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, RecipeCrafting recipe) {
                  buf.writeUtf(recipe.key);
            }

            @Override
            public RecipeCrafting fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                  String key = buf.readUtf();
                  return new RecipeCrafting(id, key);
            }
      }
}
