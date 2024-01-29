package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.world.entity.LivingEntity;
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
      public boolean backSlotDisabled(LivingEntity entity) {
            boolean armorDisables = Arrays.stream(SLOT_IDS).anyMatch(
                        slot -> Constants.DISABLES_BACK_SLOT.contains(
                                    entity.getItemBySlot(slot).getItem()));
            if (isModLoaded(TRINKETS))
                  return CurioRegistry.backSlotDisabled(entity) || armorDisables;
            return armorDisables;
      }

}
