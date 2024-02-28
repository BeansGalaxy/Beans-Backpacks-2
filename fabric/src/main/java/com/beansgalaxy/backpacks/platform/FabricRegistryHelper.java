package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.FabricMain;
import com.beansgalaxy.backpacks.Sounds;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
      public SoundEvent getSound(Kind kind, PlaySound type) {
            SoundEvent sound = type.getDefaultSoundEvent();
            if (kind == null)
                  return sound;

            switch (kind) {
                  case LEATHER -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_LEATHER;
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_LEATHER;
                              }
                              case HIT -> {
                                    return Sounds.HIT_LEATHER;
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_LEATHER;
                              }
                              case INSERT, TAKE -> {
                                    return Sounds.INSERT_LEATHER;
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_LEATHER;
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_LEATHER;
                              }
                        }
                  }
                  case METAL -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_METAL;
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_METAL;
                              }
                              case HIT -> {
                                    return Sounds.HIT_METAL;
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_METAL;
                              }
                              case INSERT -> {
                                    return Sounds.INSERT_METAL;
                              }
                              case TAKE -> {
                                    return Sounds.TAKE_METAL;
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_METAL;
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_METAL;
                              }
                        }
                  }
                  case UPGRADED -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_UPGRADED;
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_UPGRADED;
                              }
                              case HIT -> {
                                    return Sounds.HIT_UPGRADED;
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_UPGRADED;
                              }
                              case INSERT -> {
                                    return Sounds.INSERT_METAL;
                              }
                              case TAKE -> {
                                    return Sounds.TAKE_METAL;
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_UPGRADED;
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_UPGRADED;
                              }
                        }
                  }
                  case POT -> {
                        switch (type) {
                              case INSERT -> {
                                    return SoundEvents.DECORATED_POT_HIT;
                              }
                              case TAKE -> {
                                    return SoundEvents.DECORATED_POT_FALL;
                              }
                        }
                  }
            }
            return sound;
      }
}
