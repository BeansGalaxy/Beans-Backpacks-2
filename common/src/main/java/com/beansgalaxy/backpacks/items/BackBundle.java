package com.beansgalaxy.backpacks.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackBundle extends DyableBackpack {
      public BackBundle(Properties properties) {
            super(properties);
      }

      @Override
      public void onCraftedBy(ItemStack stack, Level level, Player player) {
            super.onCraftedBy(stack, level, player);
      }
}
