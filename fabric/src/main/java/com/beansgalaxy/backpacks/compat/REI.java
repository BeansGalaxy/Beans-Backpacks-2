package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.platform.Services;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.search.method.InputMethodRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;

public class REI implements REIClientPlugin {

      public void register() {
      }

      @Override
      public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
      }

      @Override
      public void registerItemComparators(ItemComparatorRegistry registry) {
            registry.registerNbt(Services.REGISTRY.getMetal());
      }
}
