package com.beansgalaxy.backpacks.compat.jei;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.recipes.Crafting;
import com.beansgalaxy.backpacks.items.recipes.Smithing;
import com.beansgalaxy.backpacks.platform.Services;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

      @Override
      public ResourceLocation getPluginUid() {
            return new ResourceLocation(Constants.MOD_ID, "jei_plugin");
      }

      @Override
      public void registerItemSubtypes(ISubtypeRegistration registration) {
            IIngredientSubtypeInterpreter<ItemStack> interpreter = (stack, context) -> {
                  CompoundTag tag = stack.getTag();
                  if (tag == null)
                        return IIngredientSubtypeInterpreter.NONE;

                  String key = tag.getString("backpack_id");
                  if (Constants.isEmpty(key))
                        return IIngredientSubtypeInterpreter.NONE;

                  return tag.getAsString();
            };

            registration.registerSubtypeInterpreter(Services.REGISTRY.getMetal(), interpreter);
      }
}
