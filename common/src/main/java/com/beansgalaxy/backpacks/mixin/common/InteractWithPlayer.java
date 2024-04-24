package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.util.ITokenProvider;

@Mixin(ServerPlayer.class)
public abstract class InteractWithPlayer extends LivingEntity implements ITokenProvider {
      protected InteractWithPlayer(EntityType<? extends LivingEntity> $$0, Level $$1) {
            super($$0, $$1);
      }

      @Override @NotNull
      public InteractionResult interact(Player player, InteractionHand hand) {
            if (InteractionHand.MAIN_HAND.equals(hand))
                  return BackSlot.openPlayerBackpackMenu(player, (ServerPlayer) (Object) this);
            return super.interact(player, hand);
      }
}
