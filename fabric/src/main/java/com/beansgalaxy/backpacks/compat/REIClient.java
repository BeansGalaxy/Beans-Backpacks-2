package com.beansgalaxy.backpacks.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;

public class REIClient implements REIClientPlugin {

      @Override
      public void registerCategories(CategoryRegistry registry) {
            registry.add(new CategoryCrafting());
      }

}
