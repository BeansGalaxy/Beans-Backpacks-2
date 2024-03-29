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
    PLACE(SoundEvents.ITEM_FRAME_PLACE, false),
    EQUIP(SoundEvents.ARMOR_EQUIP_ELYTRA, false),
    HIT(SoundEvents.PLAYER_ATTACK_WEAK, true),
    BREAK(SoundEvents.PLAYER_ATTACK_CRIT, false),
    INSERT(SoundEvents.BUNDLE_INSERT, true),
    TAKE(SoundEvents.BUNDLE_REMOVE_ONE, true),
    OPEN(SoundEvents.CHEST_OPEN, false),
    CLOSE(SoundEvents.CHEST_CLOSE, false);

    private final SoundEvent soundEvent;
    private final boolean random;

    PlaySound(SoundEvent soundEvent, boolean random) {
        this.soundEvent = soundEvent;
        this.random = random;
    }

    public boolean isRandom() {
        return random;
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
            Random rnd = new Random();
            float pitch = random ? (rnd.nextFloat() / 4f) + 0.8f : 1f;

            if (Kind.LEATHER.is(kind) && this.equals(PlaySound.TAKE))
                pitch += 0.2f;

            if (Kind.ENDER.is(kind))
                switch (this) {
                    case INSERT -> {
                        volume *= 0.1f;
                        pitch += 0.1f;
                }
                    case TAKE -> {
                        volume *= 0.1f;
                        pitch -= 0.2f;
                    }
                }

            SoundEvent soundEvent = Services.REGISTRY.getSound(kind, this);
            world.playSound(null, entity.blockPosition(), soundEvent, SoundSource.BLOCKS, volume, pitch);
        }
    }
}
