package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
      protected PlayerEntityMixin(EntityType<? extends LivingEntity> $$0, Level $$1) {
            super($$0, $$1);
      }

      @Override
      public InteractionResult interact(Player player, InteractionHand hand) {
            return BackSlot.openPlayerBackpackMenu(player, (Player) (Object) this);
      }
}
