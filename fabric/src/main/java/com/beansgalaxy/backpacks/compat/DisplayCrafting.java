package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.RecipeCrafting;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class DisplayCrafting extends BasicDisplay implements SimpleGridMenuDisplay {
      private final List<EntryIngredient> inputs;
      private final List<EntryIngredient> outputs;

      public DisplayCrafting(Optional<RecipeCrafting> recipe) {
            super(inputs(recipe.get()), outputs(recipe.get()));
            this.inputs = inputs(recipe.get());
            this.outputs = outputs(recipe.get());
      }

      public DisplayCrafting(RecipeCrafting recipeCrafting) {
            super(inputs(recipeCrafting), outputs(recipeCrafting));
            this.inputs = inputs(recipeCrafting);
            this.outputs = outputs(recipeCrafting);
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
      public List<EntryIngredient> getInputEntries() {
            return inputs;
      }

      public static List<EntryIngredient> inputs(RecipeCrafting recipe) {
            Traits traits = Traits.get(recipe.getKey());
            EntryIngredient mat = EntryIngredients.ofItems(List.of(traits.material));
            EntryIngredient bin = EntryIngredients.ofItems(List.of(traits.binder));
            EntryIngredient emp = EntryIngredient.empty();

            return List.of(
                        mat, bin, mat,
                        mat, emp, mat,
                        mat, bin, mat
            );
      }

      @Override
      public List<EntryIngredient> getOutputEntries() {
            return outputs;
      }

      public static List<EntryIngredient> outputs(RecipeCrafting recipe) {
            ItemStack output = BackpackItem.stackFromKey(recipe.getKey());
            return List.of(EntryIngredients.ofItemStacks(List.of(output)));
      }

      @Override
      public CategoryIdentifier<?> getCategoryIdentifier() {
            return CategoryCrafting.DISPLAY;
      }
}
