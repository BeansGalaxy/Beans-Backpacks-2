package com.beansgalaxy.backpacks.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackpackRecipeDisplay extends BasicDisplay {
      public BackpackRecipeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
            super(inputs, outputs);
      }

      @Override
      public List<InputIngredient<EntryStack<?>>> getInputIngredients(@Nullable AbstractContainerMenu menu, @Nullable Player player) {
            return super.getInputIngredients(menu, player);
      }

      @Override
      public CategoryIdentifier<?> getCategoryIdentifier() {
            return null;
      }
}
