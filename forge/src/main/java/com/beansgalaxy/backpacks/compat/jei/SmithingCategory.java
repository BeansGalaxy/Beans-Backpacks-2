package com.beansgalaxy.backpacks.compat.jei;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.recipes.Smithing;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class SmithingCategory implements IRecipeCategory<Smithing> {
      public static final mezz.jei.api.recipe.RecipeType<Smithing> SMITHING =
                  mezz.jei.api.recipe.RecipeType.create(Constants.MOD_ID, "smithing", Smithing.class);
      public static final String TEXTURE_GUI_PATH = "textures/jei/gui/";
      public static final String TEXTURE_GUI_VANILLA = TEXTURE_GUI_PATH + "gui_vanilla.png";
      public static final ResourceLocation RECIPE_GUI_VANILLA = new ResourceLocation(ModIds.JEI_ID, TEXTURE_GUI_VANILLA);

      private final IDrawable background;
      private final IDrawable icon;

      public SmithingCategory(IGuiHelper guiHelper) {
            background = guiHelper.createDrawable(RECIPE_GUI_VANILLA, 0, 168, 108, 18);
            icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.SMITHING_TABLE));
      }

      @Override @NotNull
      public RecipeType<Smithing> getRecipeType() {
            return SMITHING;
      }

      @Override @NotNull
      public Component getTitle() {
            return Blocks.SMITHING_TABLE.getName();
      }

      @Override @NotNull
      public IDrawable getBackground() {
            return background;
      }

      @Override @NotNull
      public IDrawable getIcon() {
            return icon;
      }

      @Override
      public void setRecipe(IRecipeLayoutBuilder builder, Smithing recipe, IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                        .addIngredients(recipe.template);

            builder.addSlot(RecipeIngredientRole.INPUT, 19, 1)
                        .addIngredients(recipe.base);

            builder.addSlot(RecipeIngredientRole.INPUT, 37, 1)
                        .addIngredients(recipe.addition);

            builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1)
                        .addItemStack(recipe.getResultItem(null));
      }

      @Override
      public boolean isHandled(@NotNull Smithing recipe) {
            return recipe instanceof Smithing && !recipe.isIncomplete();
      }
}
