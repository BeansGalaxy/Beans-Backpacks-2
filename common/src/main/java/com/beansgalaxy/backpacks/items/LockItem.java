package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.access.BaseContainerAccess;
import com.beansgalaxy.backpacks.events.PlaySound;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LockItem extends Item {
      public LockItem(Properties $$0) {
            super($$0);
      }

      @Override
      public InteractionResult useOn(UseOnContext ctx) {
            BlockEntity blockEntity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
            if (blockEntity instanceof BaseContainerBlockEntity) {
                  BaseContainerAccess baseContainerAccess = (BaseContainerAccess) blockEntity;
                  Player player = ctx.getPlayer();
                  if (!player.isLocalPlayer() && baseContainerAccess.lockContainerForOwner(player)) {
                        player.getItemInHand(ctx.getHand()).shrink(1);
                        player.playSound(PlaySound.Events.LOCK.get());
                        return InteractionResult.SUCCESS;
                  }
            }
            return super.useOn(ctx);
      }
}
