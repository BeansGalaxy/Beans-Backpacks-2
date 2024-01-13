package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.Traits;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RecipeCrafting extends ShapedRecipe {
      public static final String ID = "backpack_crafting";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<RecipeCrafting> INSTANCE = new Serializer();
      private final String key;

      public RecipeCrafting(String key) {
            super("backpack_" + key, CraftingBookCategory.EQUIPMENT, 3, 3, getIngredients(key), getResultItem(key));
            this.key = key;
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
      public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess leve) {
            String key = Traits.keyFromIngredients(container.getItem(0).getItem(), container.getItem(1).getItem());
            if (key == null || key.isEmpty())
                  return ItemStack.EMPTY;

            return BackpackItem.stackFromKey(key);
      }

      @Override
      public boolean isIncomplete() {
            return false;
      }

      @Override
      public boolean canCraftInDimensions(int $$0, int $$1) {
            return true;
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
            if (key == null || key.isEmpty())
                  return NonNullList.create();

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
            public static final Codec<RecipeCrafting> CODEC = RecordCodecBuilder.create(
                        in -> in.group(
                                    PrimitiveCodec.STRING.fieldOf("key").forGetter(RecipeCrafting::getKey)
                                          ).apply(in, RecipeCrafting::new)
            );

            @Override
            public Codec<RecipeCrafting> codec() {
                  return CODEC;
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, RecipeCrafting recipe) {
                  buf.writeUtf(recipe.key);
            }

            @Override
            public RecipeCrafting fromNetwork(FriendlyByteBuf buf) {
                  String key = buf.readUtf();
                  return new RecipeCrafting(key);
            }
      }

      public String getKey() {
            return key;
      }
}
