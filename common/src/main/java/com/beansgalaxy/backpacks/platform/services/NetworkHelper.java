package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface NetworkHelper {

      void SprintKey2S(boolean sprintKeyPressed);

      void SyncViewers2All(Entity owner, byte viewers);

      void openBackpackMenu(Player viewer, EntityAbstract owner);

      void openBackpackMenu(Player viewer, Player owner);

      MenuProvider getMenuProvider(Entity backpack);

      void syncBackSlot2C(Player owner, @Nullable ServerPlayer sender);

      void backpackInventory2C(ServerPlayer owner);

      void instantPlace2S(int i, BlockHitResult blockHitResult);

      void pickFromBackpack2S(int slot);

      void sendEnderData2C(ServerPlayer player, UUID uuid);

      void sendEnderLocations2C(ServerPlayer serverPlayer, BackData backData);

      void useCauldron2S(BlockPos pos, UseKeyEvent.Type type);

      void clearBackSlot2S(BackData backData);
}
