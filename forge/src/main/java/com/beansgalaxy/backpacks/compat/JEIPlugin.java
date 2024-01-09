package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.RecipeCrafting;
import com.beansgalaxy.backpacks.platform.Services;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

      @Override
      public ResourceLocation getPluginUid() {
            return new ResourceLocation(Constants.MOD_ID, "jei_plugin");
      }

      @Override
      public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
            registration.getCraftingCategory().addCategoryExtension(RecipeCrafting.class, CategoryCrafting::new);
      }

      @Override
      public void registerItemSubtypes(ISubtypeRegistration registration) {
            IIngredientSubtypeInterpreter<ItemStack> interpreter = (stack, context) -> {
                  CompoundTag display = stack.getTagElement("display");
                  if (display == null)
                        return IIngredientSubtypeInterpreter.NONE;

                  String key = display.getString("key");
                  if (key.isEmpty())
                        return IIngredientSubtypeInterpreter.NONE;

                  CompoundTag tag = new CompoundTag();
                  tag.putString("key", key);

                  return display.getAsString();
            };

            registration.registerSubtypeInterpreter(Services.REGISTRY.getLeather(), interpreter);
            registration.registerSubtypeInterpreter(Services.REGISTRY.getMetal(), interpreter);
            registration.registerSubtypeInterpreter(Services.REGISTRY.getUpgraded(), interpreter);
      }
}
