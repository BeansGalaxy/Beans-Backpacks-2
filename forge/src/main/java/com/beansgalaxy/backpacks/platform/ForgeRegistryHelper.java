package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.world.item.Item;

public class ForgeRegistryHelper implements RegistryHelper {
      @Override
      public Item getLeather() {
            return ForgeMain.LEATHER_BACKPACK.get();
      }

      @Override
      public Item getMetal() {
            return ForgeMain.METAL_BACKPACK.get();
      }

      @Override
      public Item getUpgraded() {
            return ForgeMain.UPGRADED_BACKPACK.get();
      }
}
