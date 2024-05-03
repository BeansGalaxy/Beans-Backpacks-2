package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.clientbound.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {
      private static boolean denyQue = false;

      @SubscribeEvent
      public static void playerInteract(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            Direction direction = event.getFace();
            if (direction == null)
                  direction = Direction.UP;

            BlockPos clickedPos = event.getPos();
            InteractionResult interact = PlaceBackpackEvent.interact(player, hand, direction, clickedPos);

            boolean consumesAction = interact.consumesAction();
            Event.Result result = consumesAction || denyQue ? Event.Result.DENY : Event.Result.DEFAULT;

            event.setUseBlock(result);
            event.setUseItem(result);

            denyQue = consumesAction && hand == InteractionHand.MAIN_HAND;
      }

      @SubscribeEvent
      public static void PlayerCloneEvent(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            if (!event.isWasDeath() || ServerSave.GAMERULES.get(Gamerules.KEEP_BACK_SLOT)) {
                  Player newPlayer = event.getEntity();

                  BackData oldBackData = BackData.get(oldPlayer);
                  BackData newBackData = BackData.get(newPlayer);
                  oldBackData.copyTo(newBackData);
                  if (oldPlayer instanceof ServerPlayer serverPlayer)
                        SendBackSlot.send(serverPlayer);
            }
      }

      @SubscribeEvent // INVENTORY CANNOT SYNC DURING PLAYER CLONE EVENT, I THINK DUE TO CLIENT PLAYER IN THE OLD BODY
      public static void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer)
                  SendBackInventory.send(serverPlayer);

      }

      @SubscribeEvent
      public static void PlayerChangeDimensions(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
                  SendBackInventory.send(player);
      }

      @SubscribeEvent
      public static void PlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
            {
                  SendBackInventory.send(player);
                  EnderStorage.get(player.level()).MAP.forEach(((uuid, enderData) -> {
                        SendEnderDisplay.send(player, uuid);
                  }));

                  EnderStorage.Location.update(player.getUUID(), player.serverLevel());
            }
      }

      @SubscribeEvent
      public static void syncDataPackEvent(OnDatapackSyncEvent event)
      {
            ServerPlayer player = event.getPlayer();
            HashMap<String, String> listMap = new HashMap<>();
            listMap.put("disables_back_slot", Constants.writeList(Constants.DISABLES_BACK_SLOT));
            listMap.put("chestplate_disabled", Constants.writeList(Constants.CHESTPLATE_DISABLED));
            listMap.put("elytra_items", Constants.writeList(Constants.ELYTRA_ITEMS));
            listMap.put("blacklist_items", Constants.writeList(Constants.BLACKLIST_ITEMS));

            // Null Player means data pack is being sent to all players
            if (player == null) {
                  NetworkPackages.S2All(new ConfigureTraits(Constants.TRAITS_MAP));
                  NetworkPackages.S2All(new ConfigureLists(listMap));
            }
            else {
                  NetworkPackages.S2C(new ConfigureTraits(Constants.TRAITS_MAP), player);
                  NetworkPackages.S2C(new ConfigureLists(listMap), player);
            }

            String syncedPlayers = player == null ? "all players" : "\"" + player.getDisplayName().getString() + "\"";
            Constants.LOG.info("Syncing {} data to {}", Constants.MOD_ID, syncedPlayers);
      }

      @SubscribeEvent
      public static void serverStartedEvent(ServerStartedEvent event) {
            MinecraftServer server = event.getServer();
            ServerSave.getSave(server, true);
      }

      @SubscribeEvent
      public static void serverStoppingEvent(ServerStoppingEvent event) {
            MinecraftServer server = event.getServer();
            ServerSave.getSave(server, true);
      }

      @SubscribeEvent
      public static void registerCommands(RegisterCommandsEvent event) {
            RegisterCommands.register(event.getDispatcher());
      }
}
