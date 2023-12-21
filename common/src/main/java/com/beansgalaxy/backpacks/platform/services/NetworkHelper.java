package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface NetworkHelper {

      void SprintKey(boolean sprintKeyPressed);

      void SyncViewers(Entity owner, byte viewers);

      void openBackpackMenu(Player player, Backpack entity);

      MenuProvider getMenuProvider(Backpack backpack);
}
