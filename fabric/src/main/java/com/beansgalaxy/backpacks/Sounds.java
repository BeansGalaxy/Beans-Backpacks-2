package com.beansgalaxy.backpacks;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class Sounds {
      public static final SoundEvent PLACE_LEATHER = registerSoundEvent("place_leather");
      public static final SoundEvent EQUIP_LEATHER = registerSoundEvent("equip_leather");
      public static final SoundEvent HIT_LEATHER = registerSoundEvent("hit_leather");
      public static final SoundEvent BREAK_LEATHER = registerSoundEvent("break_leather");
      public static final SoundEvent INSERT_LEATHER = registerSoundEvent("insert_leather");
      public static final SoundEvent OPEN_LEATHER = registerSoundEvent("open_leather");
      public static final SoundEvent CLOSE_LEATHER = registerSoundEvent("close_leather");

      public static final SoundEvent PLACE_METAL = registerSoundEvent("place_metal");
      public static final SoundEvent EQUIP_METAL = registerSoundEvent("equip_metal");
      public static final SoundEvent HIT_METAL = registerSoundEvent("hit_metal");
      public static final SoundEvent BREAK_METAL = registerSoundEvent("break_metal");
      public static final SoundEvent INSERT_METAL = registerSoundEvent("insert_metal");
      public static final SoundEvent TAKE_METAL = registerSoundEvent("take_metal");
      public static final SoundEvent OPEN_METAL = registerSoundEvent("open_metal");
      public static final SoundEvent CLOSE_METAL = registerSoundEvent("close_metal");

      public static final SoundEvent PLACE_UPGRADED = registerSoundEvent("place_upgraded");
      public static final SoundEvent EQUIP_UPGRADED = registerSoundEvent("equip_upgraded");
      public static final SoundEvent HIT_UPGRADED = registerSoundEvent("hit_upgraded");
      public static final SoundEvent BREAK_UPGRADED = registerSoundEvent("break_upgraded");
      public static final SoundEvent OPEN_UPGRADED = registerSoundEvent("open_upgraded");
      public static final SoundEvent CLOSE_UPGRADED = registerSoundEvent("close_upgraded");

      private static SoundEvent registerSoundEvent(String name) {
            ResourceLocation id = new ResourceLocation(Constants.MOD_ID, name);
            SoundEvent event = SoundEvent.createVariableRangeEvent(id);
            return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
      }

      public static void register() {
            Constants.LOG.info("Registering Sounds for " + Constants.MOD_ID);
      }
}
