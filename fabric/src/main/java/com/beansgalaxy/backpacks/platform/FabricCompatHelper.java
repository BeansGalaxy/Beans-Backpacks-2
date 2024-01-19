package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FabricCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return FabricLoader.getInstance().isModLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(BackData backData, ItemStack stack, Player owner) {
            if (isModLoaded(TRINKETS))
                  TrinketsRegistry.setBackStack(stack, owner);
            else
                  backData.backSlot.container.setItem(0, stack);
      }

      @Override
      public ItemStack getBackSlotItem(Player owner, ItemStack stack) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.getBackStack(owner, stack);
            return stack;
      }
}
