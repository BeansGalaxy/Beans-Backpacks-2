package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.clientbound.Packet2C;
import com.beansgalaxy.backpacks.network.serverbound.Packet2S;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;

public interface NetworkHelper {

      void send(Network2S network, Packet2S msg);

      void send(Network2C network, Packet2C msg, ServerPlayer to);

      void send(Network2C network2C, Packet2C msg, MinecraftServer server);

      void openBackpackMenu(Player viewer, EntityAbstract owner);

      void openBackpackMenu(Player viewer, BackData owner);

      MenuProvider getMenuProvider(EntityAccess backpack);
}
