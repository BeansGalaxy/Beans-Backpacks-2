package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface CompatHelper {
      String CURIOS = "curios";
      String TRINKETS = "trinkets";

      boolean isModLoaded(String namespace);

      default boolean anyModsLoaded(String[] namespaces) {
            for (String namespace: namespaces)
                  if (isModLoaded(namespace))
                        return true;
            return false;
      }

      default boolean graveModLoaded() {
            return anyModsLoaded(new String[]{"universal-graves", "yigd", "gravestones"});
      }

      void setBackSlotItem(BackData data, ItemStack stack);

      ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem);

      boolean backSlotDisabled(Player owner);
}
