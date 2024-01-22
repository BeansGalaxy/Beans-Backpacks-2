package com.beansgalaxy.backpacks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sounds {
      public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
                  DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MOD_ID);

      public static final RegistryObject<SoundEvent> PLACE_LEATHER = registerSoundEvents("place_leather");
      public static final RegistryObject<SoundEvent> EQUIP_LEATHER = registerSoundEvents("equip_leather");
      public static final RegistryObject<SoundEvent> HIT_LEATHER = registerSoundEvents("hit_leather");
      public static final RegistryObject<SoundEvent> BREAK_LEATHER = registerSoundEvents("break_leather");
      public static final RegistryObject<SoundEvent> INSERT_LEATHER = registerSoundEvents("insert_leather");
      public static final RegistryObject<SoundEvent> OPEN_LEATHER = registerSoundEvents("open_leather");
      public static final RegistryObject<SoundEvent> CLOSE_LEATHER = registerSoundEvents("close_leather");

      public static final RegistryObject<SoundEvent> PLACE_METAL = registerSoundEvents("place_metal");
      public static final RegistryObject<SoundEvent> EQUIP_METAL = registerSoundEvents("equip_metal");
      public static final RegistryObject<SoundEvent> HIT_METAL = registerSoundEvents("hit_metal");
      public static final RegistryObject<SoundEvent> BREAK_METAL = registerSoundEvents("break_metal");
      public static final RegistryObject<SoundEvent> INSERT_METAL = registerSoundEvents("insert_metal");
      public static final RegistryObject<SoundEvent> TAKE_METAL = registerSoundEvents("take_metal");
      public static final RegistryObject<SoundEvent> OPEN_METAL = registerSoundEvents("open_metal");
      public static final RegistryObject<SoundEvent> CLOSE_METAL = registerSoundEvents("close_metal");

      public static final RegistryObject<SoundEvent> PLACE_UPGRADED = registerSoundEvents("place_upgraded");
      public static final RegistryObject<SoundEvent> EQUIP_UPGRADED = registerSoundEvents("equip_upgraded");
      public static final RegistryObject<SoundEvent> HIT_UPGRADED = registerSoundEvents("hit_upgraded");
      public static final RegistryObject<SoundEvent> BREAK_UPGRADED = registerSoundEvents("break_upgraded");
      public static final RegistryObject<SoundEvent> OPEN_UPGRADED = registerSoundEvents("open_upgraded");
      public static final RegistryObject<SoundEvent> CLOSE_UPGRADED = registerSoundEvents("close_upgraded");

      private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
            return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, name)));
      }

      public static void register(IEventBus bus) {
            SOUND_EVENTS.register(bus);
      }
}
