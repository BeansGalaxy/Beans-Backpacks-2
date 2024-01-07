package com.beansgalaxy.backpacks.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;

public class REIClient implements REIClientPlugin {

      @Override
      public void registerCategories(CategoryRegistry registry) {
            registry.add(new CategoryCrafting());
      }

      @Override
      public void registerDisplays(DisplayRegistry registry) {
//            registry.registerRecipeFiller(RecipeCrafting.class, RecipeType.CRAFTING, DisplayCrafting::new);

//            registry.registerFiller(RecipeCrafting.class, DisplayCrafting::new);
//
//            for (String key: Constants.REGISTERED_DATA.keySet())
//                  registry.add(new RecipeCrafting(key));
      }
}
