package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.core.BackData;
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

      void setBackSlotItem(BackData data, ItemStack stack, Player owner);

      ItemStack getBackSlotItem(Player owner, ItemStack item);
}
