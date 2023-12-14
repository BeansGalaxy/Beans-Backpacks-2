package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.FabricMain;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.world.item.Item;

public class FabricRegistryHelper implements RegistryHelper {

      @Override
      public Item getLeather() {
            return FabricMain.LEATHER_BACKPACK.asItem();
      }

      @Override
      public Item getMetal() {
            return FabricMain.METAL_BACKPACK.asItem();
      }

      @Override
      public Item getUpgraded() {
            return FabricMain.UPGRADED_BACKPACK.asItem();
      }
}
