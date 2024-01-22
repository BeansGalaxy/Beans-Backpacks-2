package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

public class FabricCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return FabricLoader.getInstance().isModLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(BackData backData, ItemStack stack) {
            if (isModLoaded(TRINKETS))
                  TrinketsRegistry.setBackStack(stack, backData);
      }

      @Override
      public ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.getBackStack(backData, defaultItem);
            return defaultItem;
      }
}
