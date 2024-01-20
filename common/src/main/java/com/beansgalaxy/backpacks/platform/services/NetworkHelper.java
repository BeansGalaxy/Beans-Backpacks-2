package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public interface NetworkHelper {

      void SprintKey(boolean sprintKeyPressed);

      void SyncViewers(Entity owner, byte viewers);

      void openBackpackMenu(Player viewer, Backpack owner);

      void openBackpackMenu(Player viewer, Player owner);

      MenuProvider getMenuProvider(Entity backpack);

      void SyncBackSlot(ServerPlayer owner);

      void backpackInventory2C(ServerPlayer owner);

      void instantPlace(int i, BlockHitResult blockHitResult);
}
