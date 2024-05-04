package com.beansgalaxy.backpacks.events;

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

    public void at(Entity entity, Kind kind) {
        this.at(entity, kind, 1.2f);
    }

    public void at(Entity entity, Kind kind, float volume) {
        Level world = entity.level();
        if (!world.isClientSide) {
            Playable sound = getSound(kind);
            world.playSound(null, entity.blockPosition(), sound.event, SoundSource.BLOCKS, volume * sound.volume(), sound.pitch());
        }
    }

    public record Playable(SoundEvent event, float volume, float pitch) {}

    public Playable getSound(Kind kind) {
        switch (kind) {
            case LEATHER, WINGED -> {
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
            case METAL, UPGRADED -> {
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
            case POT -> {
                switch (this) {
                    case INSERT -> {
                        return new Playable(SoundEvents.DECORATED_POT_HIT, 1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                    case TAKE -> {
                        return new Playable(SoundEvents.DECORATED_POT_FALL, 1f, new Random().nextFloat(0.7f, 1.1f));
                    }
                }
            }
            case ENDER -> {
                switch (this) {
                    case OPEN -> {
                        return new Playable(SoundEvents.ENDER_CHEST_OPEN, 0.3f, 1f);
                    }
                    case CLOSE -> {
                        return new Playable(SoundEvents.ENDER_CHEST_CLOSE, 0.5f, 1f);
                    }
                    case TAKE-> {
                        return new Playable(SoundEvents.ENDERMAN_TELEPORT, 0.1f, new Random().nextFloat(0.8f, 0.9f));
                    }
                    case INSERT -> {
                        return new Playable(SoundEvents.ENDERMAN_TELEPORT, 0.1f, new Random().nextFloat(0.9f, 1.1f));
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
        METAL_CLOSE    ("metal_close");

        public final String id;

        Events(String id) {
            this.id = id;
        }

        private SoundEvent get() {
            return Services.REGISTRY.soundEvent(id);
        }

        private Playable playable(float volume, float pitch) {
            return new Playable(get(), volume, pitch);
        }
    }
}
