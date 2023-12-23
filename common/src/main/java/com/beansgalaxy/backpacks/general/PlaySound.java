package com.beansgalaxy.backpacks.general;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public enum PlaySound {
    PLACE(SoundEvents.ITEM_FRAME_PLACE),
    EQUIP(SoundEvents.ARMOR_EQUIP_ELYTRA),
    DROP(SoundEvents.ITEM_FRAME_BREAK),
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

    public SoundEvent get() {
        return soundEvent;
    }

    public void at(Entity entity) {
        this.at(entity, 0.4f);
    }

    public void at(Entity entity, float volume) {
        Level world = entity.level();
        if (!world.isClientSide) {
            world.playSound(null, entity.blockPosition(), soundEvent, SoundSource.BLOCKS, volume, 1f);
        }
    }
}
