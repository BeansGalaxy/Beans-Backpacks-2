package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface RegistryHelper {

      Item getLeather();

      Item getMetal();

      Item getUpgraded();

      Item getWinged();

      Item getEnder();

      Item getBigBundle();

      EntityType<? extends Entity> getGeneralEntity();

      EntityType<? extends Entity> getEnderEntity();

      EntityType<? extends Entity> getWingedEntity();

      MenuType<?> getMenu();

      void triggerEquipAny(ServerPlayer player);

      void triggerPlace(ServerPlayer player, String key);

      void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special);

      SoundEvent soundEvent(String name);
}
