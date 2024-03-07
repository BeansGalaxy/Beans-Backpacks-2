package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class ElytraEvent {
      public static boolean doesFlyFall(boolean tickElytra, Player player) {
            ItemStack backStack = BackData.get(player).getStack();
            int damageValue = backStack.getDamageValue();
            int maxDamage = backStack.getMaxDamage();
            boolean flyEnabled = damageValue < maxDamage - 1;
            boolean wings = Kind.isWings(backStack);
            if (!wings || !flyEnabled)
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
            return true;
      }
}
