package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class ForgeCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return ModList.get().isLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(ItemStack stack, Player owner) {
            if (isModLoaded(CURIOS))
                  CurioRegistry.setBackStack(stack, owner);
      }

      @Override
      public ItemStack getBackSlotItem(Player owner, ItemStack stack) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.getBackStackItem(owner, stack);
            return stack;
      }

}
