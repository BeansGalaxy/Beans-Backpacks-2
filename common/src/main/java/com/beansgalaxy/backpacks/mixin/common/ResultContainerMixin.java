package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.items.recipes.SuperSpecialRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResultContainer.class)
public abstract class ResultContainerMixin implements RecipeHolder {

      @Override
      public boolean setRecipeUsed(Level $$0, ServerPlayer player, Recipe<?> craftingRecipe) {
            if (craftingRecipe instanceof SuperSpecialRecipe recipe) {
                  if (!recipe.isSuperSpecialPlayer(player))
                        return false;
            }
            return RecipeHolder.super.setRecipeUsed($$0, player, craftingRecipe);
      }
}
