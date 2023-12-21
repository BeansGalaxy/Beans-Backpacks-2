package com.beansgalaxy.backpacks.platform.services;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public interface RegistryHelper {

      Item getLeather();

      Item getMetal();

      Item getUpgraded();

      EntityType<? extends Entity> getEntity();

      MenuType<?> getMenu();
}
