package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.entity.EntityGeneral;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
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
      public Item getWinged() {
            return ForgeMain.WINGED_BACKPACK.get();
      }

      @Override
      public Item getEnder() {
            return ForgeMain.ENDER_BACKPACK.get();
      }

      @Override
      public Item getBigBundle() {
            return ForgeMain.BACK_BUNDLE.get();
      }

      @Override
      public Item getLock() {
            return ForgeMain.LOCK.get();
      }

      @Override
      public EntityType<EntityGeneral> getGeneralEntity() {
            return ForgeMain.ENTITY_GENERAL.get();
      }

      @Override
      public EntityType<? extends Entity> getEnderEntity() {
            return ForgeMain.ENTITY_ENDER.get();
      }

      @Override
      public EntityType<? extends Entity> getWingedEntity() {
            return ForgeMain.ENTITY_WINGED.get();
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
      public void triggerPlace(ServerPlayer player, String key) {
            ForgeMain.PLACE.trigger(player, key);
      }

      @Override
      public void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special) {
            if (player != null)
                  ForgeMain.SPECIAL.trigger(player, special);
      }

      @Override
      public SoundEvent soundEvent(String name) {
            return ForgeMain.SOUNDS.get(name).get();
      }

}
