package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.FabricMain;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
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

      @Override
      public Item getWinged() {
            return FabricMain.WINGED_BACKPACK.asItem();
      }

      @Override
      public Item getEnder() {
            return FabricMain.ENDER_BACKPACK.asItem();
      }

      @Override
      public EntityType<? extends Entity> getGeneralEntity() {
            return FabricMain.BACKPACK_ENTITY;
      }

      @Override
      public EntityType<? extends Entity> getEnderEntity() {
            return FabricMain.ENDER_ENTITY;
      }

      @Override
      public EntityType<? extends Entity> getWingedEntity() {
            return FabricMain.WINGED_ENTITY;
      }

      @Override
      public MenuType<?> getMenu() {
            return FabricMain.BACKPACK_MENU;
      }

      @Override
      public void triggerEquipAny(ServerPlayer player) {
            FabricMain.EQUIP_ANY.trigger(player);
      }

      @Override
      public void triggerPlace(ServerPlayer player, String key) {
            FabricMain.PLACE.trigger(player, key);
      }

      @Override
      public void triggerSpecial(ServerPlayer player, SpecialCriterion.Special special) {
            FabricMain.SPECIAL.trigger(player, special);
      }

      @Override
      public SoundEvent soundEvent(String id) {
            return FabricMain.SOUNDS.get(id);
      }

}
