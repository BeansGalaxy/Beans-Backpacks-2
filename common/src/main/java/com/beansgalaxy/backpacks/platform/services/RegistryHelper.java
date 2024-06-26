package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public interface RegistryHelper {

      Item getLeather();

      Item getMetal();

      Item getWinged();

      Item getEnder();

      Item getBigBundle();

      Item getLock();

      EntityType<? extends Entity> getGeneralEntity();

      EntityType<? extends Entity> getEnderEntity();

      EntityType<? extends Entity> getWingedEntity();

      MenuType<?> getMenu();

      void triggerEquipAny(ServerPlayer player);

      void triggerPlace(ServerPlayer player, String key);

      void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special);

      SoundEvent soundEvent(String name);
}
