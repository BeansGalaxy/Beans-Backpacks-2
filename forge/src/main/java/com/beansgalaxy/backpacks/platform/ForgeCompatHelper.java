package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;

public class ForgeCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return ModList.get().isLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(BackData backData, ItemStack stack) {
            if (isModLoaded(CURIOS))
                  CurioRegistry.setBackStack(stack, backData.owner);
      }

      @Override
      public ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.getBackStackItem(backData, defaultItem);
            return defaultItem;
      }

      @Override
      public void getEquipped(NonNullList<ItemStack> equipped, Player player) {
            if (isModLoaded(CURIOS))
                  CurioRegistry.getEquipped(equipped, player);
      }

}
