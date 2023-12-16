package com.beansgalaxy.backpacks.general;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    public void toClient(Player player) {
        if (!player.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().play(
                new SimpleSoundInstance(this.soundEvent.getLocation(), SoundSource.PLAYERS, 0.7f, player.level().random.nextFloat() * 0.1f + 0.8f,
                            SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.LINEAR, 0.0, 0.0, 0.0, true)
                );
        }
    }

    public static void registerAll() {
        for (PlaySound sound : PlaySound.values()) {
            PlaySound.register(sound.toString().toLowerCase());
        }
    }

    private static SoundEvent register(String name) {
        ResourceLocation id = new ResourceLocation(Constants.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
