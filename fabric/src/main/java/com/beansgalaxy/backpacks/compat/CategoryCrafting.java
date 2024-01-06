package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class CategoryCrafting implements DisplayCategory<SimpleGridMenuDisplay> {
      public static final CategoryIdentifier<DisplayCrafting> DISPLAY =
                  CategoryIdentifier.of(Constants.MOD_ID, "crafting");

      @Override
      public CategoryIdentifier<? extends SimpleGridMenuDisplay> getCategoryIdentifier() {
            return DISPLAY;
      }

      @Override
      public Component getTitle() {
            return Component.translatable("category.rei.crafting");
      }

      @Override
      public Renderer getIcon() {
            return EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(Blocks.CRAFTING_TABLE));
      }

}
