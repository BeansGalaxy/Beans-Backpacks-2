package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
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

      @Override
      public EntityType<BackpackEntity> getEntity() {
            return ForgeMain.ENTITY.get();
      }

      @Override
      public MenuType<?> getMenu() {
            return ForgeMain.MENU.get();
      }

      @Override
      public void triggerEquipAny(ServerPlayer player) {
            ForgeMain.EQUIP_ANY.trigger(player);
      }

      @Override
      public void triggerPlace(ServerPlayer player) {
            ForgeMain.PLACE.trigger(player);
      }

      @Override
      public void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special) {
            ForgeMain.SPECIAL.trigger(player, special);
      }
}
