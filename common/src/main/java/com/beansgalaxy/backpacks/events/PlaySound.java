package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Random;

public enum PlaySound {
    PLACE(SoundEvents.ITEM_FRAME_PLACE),
    EQUIP(SoundEvents.ARMOR_EQUIP_ELYTRA),
    HIT(SoundEvents.PLAYER_ATTACK_WEAK),
    BREAK(SoundEvents.PLAYER_ATTACK_CRIT),
    INSERT(SoundEvents.BUNDLE_INSERT),
    TAKE(SoundEvents.BUNDLE_REMOVE_ONE),
    OPEN(SoundEvents.CHEST_OPEN),
    CLOSE(SoundEvents.CHEST_CLOSE);

    private final SoundEvent soundEvent;

    PlaySound(SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
    }

    public SoundEvent getDefaultSoundEvent() {
        return soundEvent;
    }

    public void at(Entity entity, Traits.Sound sound) {
        this.at(entity, sound, 1.2f);
    }

    public void at(Entity entity, Traits.Sound sound, float volume) {
        Level world = entity.level();
        if (!world.isClientSide) {
            Playable playable = getSound(sound);
            world.playSound(null, entity.blockPosition(), playable.event, SoundSource.BLOCKS, volume * playable.volume(), playable.pitch());
        }
    }

    public record Playable(SoundEvent event, float volume, float pitch) {}

    public Playable getSound(Traits.Sound sound) {
        switch (sound) {
            case SOFT -> {
                switch (this) {
                    case PLACE -> {
                        return Events.LEATHER_PLACE.playable(1f, 1f);
                    }
                    case EQUIP -> {
                        return Events.LEATHER_EQUIP.playable(1f, 1f);
                    }
                    case HIT -> {
                        return Events.LEATHER_HIT.playable(1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                    case BREAK -> {
                        return Events.LEATHER_BREAK.playable(1f, 1f);
                    }
                    case INSERT -> {
                        return Events.LEATHER_INSERT.playable(1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                    case TAKE -> {
                        return Events.LEATHER_INSERT.playable(1f, new Random().nextFloat(1, 1.3f));
                    }
                    case OPEN -> {
                        return Events.LEATHER_OPEN.playable(1f, 1f);
                    }
                    case CLOSE -> {
                        return Events.LEATHER_CLOSE.playable(1f, 1f);
                    }
                }
            }
            case HARD -> {
                switch (this) {
                    case PLACE -> {
                        return Events.METAL_PLACE.playable(1f, 1f);
                    }
                    case EQUIP -> {
                        return Events.METAL_EQUIP.playable(1f, 1f);
                    }
                    case HIT -> {
                        return Events.METAL_HIT.playable(1f, new Random().nextFloat(0.8f, 1.1f));
                    }
                    case BREAK -> {
                        return Events.METAL_BREAK.playable(1f, 1f);
                    }
                    case INSERT -> {
                        return Events.METAL_INSERT.playable(1f, new Random().nextFloat(0.8f, 1.1f));
                    }
                    case TAKE -> {
                        return Events.METAL_TAKE.playable(0.7f, new Random().nextFloat(0.8f, 1.1f));
                    }
                    case OPEN -> {
                        return Events.METAL_OPEN.playable(1f, 1f);
                    }
                    case CLOSE -> {
                        return Events.METAL_CLOSE.playable(1f, 1f);
                    }
                }
            }
            case CLAY -> {
                switch (this) {
                    case HIT -> {
                        return Events.POT_HIT.playable(1f, 1f);
                    }
                    case INSERT -> {
                        return Events.POT_INSERT.playable(1f, 1f);
                    }
                    case TAKE -> {
                        return Events.POT_TAKE.playable(1f, 1f);
                    }
                }
            }
            case VWOOMP -> {
                switch (this) {
                    case PLACE -> {
                        return Events.ENDER_PLACE.playable(1f, 1f);
                    }
                    case EQUIP -> {
                        return Events.ENDER_EQUIP.playable(1f, 1f);
                    }
                    case HIT -> {
                        return Events.ENDER_HIT.playable(1f, new Random().nextFloat(0.9f, 1.1f));
                    }
                    case BREAK -> {
                        return Events.ENDER_BREAK.playable(1f, 1f);
                    }
                    case OPEN -> {
                        return Events.ENDER_OPEN.playable(1f, 1f);
                    }
                    case CLOSE -> {
                        return Events.ENDER_CLOSE.playable(1f, 1f);
                    }
                    case TAKE-> {
                        return Events.ENDER_TAKE.playable(1f, new Random().nextFloat(0.9f, 1.1f));
                    }
                    case INSERT -> {
                        return Events.ENDER_INSERT.playable(1f, new Random().nextFloat(0.9f, 1.1f));
                    }
                }
            }
            case CRUNCH -> {
                switch (this) {
                    case PLACE -> {
                        return Events.WINGED_PLACE.playable(1f, 1f);
                    }
                    case EQUIP -> {
                        return Events.WINGED_EQUIP.playable(1f, 1f);
                    }
                    case HIT -> {
                        return Events.WINGED_HIT.playable(1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                    case BREAK -> {
                        return Events.WINGED_BREAK.playable(1f, 1f);
                    }
                    case INSERT -> {
                        return Events.LEATHER_INSERT.playable(1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                    case TAKE -> {
                        return Events.LEATHER_INSERT.playable(1f, new Random().nextFloat(1, 1.3f));
                    }
                    case OPEN -> {
                        return Events.WINGED_OPEN.playable(1f, 1f);
                    }
                    case CLOSE -> {
                        return Events.WINGED_CLOSE.playable(1f, 1f);
                    }
                }
            }
        }
        return new Playable(getDefaultSoundEvent(), 1f, 1f);
    }

    public enum Events {
        LEATHER_PLACE  ("leather_place"),
        LEATHER_EQUIP  ("leather_equip"),
        LEATHER_HIT    ("leather_hit"),
        LEATHER_BREAK  ("leather_break"),
        LEATHER_INSERT ("leather_insert"),
        LEATHER_OPEN   ("leather_open"),
        LEATHER_CLOSE  ("leather_close"),
        METAL_PLACE    ("metal_place"),
        METAL_EQUIP    ("metal_equip"),
        METAL_HIT      ("metal_hit"),
        METAL_BREAK    ("metal_break"),
        METAL_INSERT   ("metal_insert"),
        METAL_TAKE     ("metal_take"),
        METAL_OPEN     ("metal_open"),
        METAL_CLOSE    ("metal_close"),
        ENDER_PLACE    ("ender_place"),
        ENDER_EQUIP    ("ender_equip"),
        ENDER_HIT      ("ender_hit"),
        ENDER_BREAK    ("ender_break"),
        ENDER_INSERT   ("ender_insert"),
        ENDER_TAKE     ("ender_take"),
        ENDER_OPEN     ("ender_open"),
        ENDER_CLOSE    ("ender_close"),
        WINGED_PLACE   ("winged_place"),
        WINGED_EQUIP   ("winged_equip"),
        WINGED_HIT     ("winged_hit"),
        WINGED_BREAK   ("winged_break"),
        WINGED_OPEN    ("winged_open"),
        WINGED_CLOSE   ("winged_close"),
        POT_HIT        ("pot_hit"),
        POT_INSERT     ("pot_insert"),
        POT_TAKE       ("pot_take"),
        LOCK           ("lock_backpack"),
        UNLOCK         ("unlock_backpack");

        public final String id;

        Events(String id) {
            this.id = id;
        }

        public SoundEvent get() {
            return Services.REGISTRY.soundEvent(id);
        }

        private Playable playable(float volume, float pitch) {
            return new Playable(get(), volume, pitch);
        }
    }
}
