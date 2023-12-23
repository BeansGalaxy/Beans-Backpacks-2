package com.beansgalaxy.backpacks.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class PlayerInteractEvent implements UseBlockCallback {
      @Override
      public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
            Direction direction = hitResult.getDirection();
            BlockPos blockPos = hitResult.getBlockPos();
            return PlaceBackpackEvent.interact(player, hand, direction, blockPos);
      }
}
