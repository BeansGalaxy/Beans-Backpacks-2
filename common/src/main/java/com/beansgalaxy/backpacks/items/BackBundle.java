package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.data.ServerSave;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackBundle extends DyableBackpack {
      public BackBundle(Properties properties) {
            super(properties);
      }

      @Override
      public void onCraftedBy(ItemStack stack, Level level, Player player) {
            if (!level.isClientSide) {
                  ServerSave save = ServerSave.getSave(player.getServer(), false);
                  if (!save.isSuperSpecial(player))
                        stack.setCount(0);
            }
            super.onCraftedBy(stack, level, player);
      }
}
