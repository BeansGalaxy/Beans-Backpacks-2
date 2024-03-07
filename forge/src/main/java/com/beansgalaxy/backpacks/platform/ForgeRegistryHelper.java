package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.Sounds;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.EntityGeneral;
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
            ForgeMain.SPECIAL.trigger(player, special);
      }

      @Override
      public SoundEvent getSound(Kind kind, PlaySound type) {
            SoundEvent sound = type.getDefaultSoundEvent();
            if (kind == null)
                  return sound;

            switch (kind) {
                  case LEATHER, WINGED -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_LEATHER.get();
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_LEATHER.get();
                              }
                              case HIT -> {
                                    return Sounds.HIT_LEATHER.get();
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_LEATHER.get();
                              }
                              case INSERT, TAKE -> {
                                    return Sounds.INSERT_LEATHER.get();
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_LEATHER.get();
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_LEATHER.get();
                              }
                        }
                  }
                  case METAL -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_METAL.get();
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_METAL.get();
                              }
                              case HIT -> {
                                    return Sounds.HIT_METAL.get();
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_METAL.get();
                              }
                              case INSERT -> {
                                    return Sounds.INSERT_METAL.get();
                              }
                              case TAKE -> {
                                    return Sounds.TAKE_METAL.get();
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_METAL.get();
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_METAL.get();
                              }
                        }
                  }
                  case UPGRADED -> {
                        switch (type) {
                              case PLACE -> {
                                    return Sounds.PLACE_UPGRADED.get();
                              }
                              case EQUIP -> {
                                    return Sounds.EQUIP_UPGRADED.get();
                              }
                              case HIT -> {
                                    return Sounds.HIT_UPGRADED.get();
                              }
                              case BREAK -> {
                                    return Sounds.BREAK_UPGRADED.get();
                              }
                              case INSERT -> {
                                    return Sounds.INSERT_METAL.get();
                              }
                              case TAKE -> {
                                    return Sounds.TAKE_METAL.get();
                              }
                              case OPEN -> {
                                    return Sounds.OPEN_UPGRADED.get();
                              }
                              case CLOSE -> {
                                    return Sounds.CLOSE_UPGRADED.get();
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
                  case ENDER -> {
                        switch (type) {
                              case OPEN -> {
                                    return SoundEvents.ENDER_CHEST_OPEN;
                              }
                              case CLOSE -> {
                                    return SoundEvents.ENDER_CHEST_CLOSE;
                              }
                              case TAKE, INSERT -> {
                                    return SoundEvents.ENDERMAN_TELEPORT;
                              }
                        }
                  }
            }
            return sound;
      }
}
