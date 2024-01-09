package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.RecipeCrafting;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CategoryCrafting implements ICraftingCategoryExtension {
      private final RecipeCrafting recipe;

      public CategoryCrafting(RecipeCrafting recipe) {
            this.recipe = recipe;
      }

      @Override
      public int getWidth() {
            return 3;
      }

      @Override
      public int getHeight() {
            return 3;
      }

      @Override
      public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
            Item materialItem = recipe.getMaterial();
            Item binderItem = recipe.getBinder();
            List<ItemStack> mat = List.of(materialItem.getDefaultInstance());
            List<ItemStack> bin = List.of(binderItem.getDefaultInstance());
            List<ItemStack> emp = List.of(ItemStack.EMPTY);

            List<List<ItemStack>> stacks = List.of(
                        mat, bin, mat,
                        mat, emp, mat,
                        mat, bin, mat
            );

            craftingGridHelper.createAndSetInputs(builder, stacks, 3, 3);

            ItemStack outputStack = BackpackItem.stackFromKey(recipe.getKey());
            List<ItemStack> output = List.of(outputStack);
            craftingGridHelper.createAndSetOutputs(builder, output);
      }
}
