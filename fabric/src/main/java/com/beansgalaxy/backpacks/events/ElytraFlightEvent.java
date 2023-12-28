package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

public class ElytraFlightEvent implements EntityElytraEvents.Custom {
      @Override
      public boolean useCustomElytra(LivingEntity entity, boolean tickElytra) {

            if (Constants.CHESTPLATE_DISABLED.contains(Items.ELYTRA) && entity instanceof Player player)
            {
                  ItemStack backStack = BackSlot.get(player).getItem();
                  if (!backStack.is(Items.ELYTRA) || !ElytraItem.isFlyEnabled(backStack))
                        return false;

                  if (tickElytra)
                  {
                        int i = player.getFallFlyingTicks() + 1;
                        if (!player.level().isClientSide() && i % 10 == 0)
                        {
                              int j = i / 10;
                              if (j % 2 == 0)
                                    backStack.hurtAndBreak(1, player, in -> in.broadcastBreakEvent(EquipmentSlot.CHEST));
                              player.gameEvent(GameEvent.ELYTRA_GLIDE);
                        }
                  }
                  return backStack.getItem() == Items.ELYTRA;
            }
            return false;
      }
}
