package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import net.minecraft.server.level.ServerPlayer;
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

      void triggerEquipAny(ServerPlayer player);

      void triggerPlace(ServerPlayer player);

      void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special);

}
