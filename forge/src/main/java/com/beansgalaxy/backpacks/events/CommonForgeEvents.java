package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.*;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.client.ConfigureLists2C;
import com.beansgalaxy.backpacks.network.client.ConfigureTraits2C;
import com.beansgalaxy.backpacks.network.client.SendEnderData2C;
import com.beansgalaxy.backpacks.network.client.SyncBackSlot2C;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

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
      public static void LivingEntityDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof Player player && !ConfigHelper.keepBackSlot(player.level()))
                  BackData.get(player).drop();
      }

      @SubscribeEvent
      public static void PlayerCloneEvent(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            if (!event.isWasDeath() || ConfigHelper.keepBackSlot(oldPlayer.level())) {
                  Player newPlayer = event.getEntity();

                  BackData.get(oldPlayer).copyTo(BackData.get(newPlayer));
                  if (oldPlayer instanceof ServerPlayer serverPlayer)
                        Services.NETWORK.syncBackSlot2All(serverPlayer);
            }
      }

      @SubscribeEvent // INVENTORY CANNOT SYNC DURING PLAYER CLONE EVENT, I THINK DUE TO CLIENT PLAYER IN THE OLD BODY
      public static void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer)
                  Services.NETWORK.backpackInventory2C(serverPlayer);

      }

      @SubscribeEvent
      public static void PlayerChangeDimensions(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
                  Services.NETWORK.backpackInventory2C(player);
      }

      @SubscribeEvent
      public static void PlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
            {
                  Services.NETWORK.backpackInventory2C(player);

                  ItemStack stack = BackData.get(player).getStack();
                  NetworkPackages.S2C(new SyncBackSlot2C(player.getUUID(), stack), player);

                  EnderStorage.get().MAPPED_DATA.forEach(((uuid, enderData) -> {
                        NetworkPackages.S2C(new SendEnderData2C(uuid, enderData), player);
                  }));

                  EnderStorage.Location.update(player.getUUID(), player.serverLevel());
            }
      }

      @SubscribeEvent
      public static void syncDataPackEvent(OnDatapackSyncEvent event)
      {
            ServerPlayer player = event.getPlayer();
            Map<String, CompoundTag> traitMap = new HashMap<>();

            for (String key : Constants.TRAITS_MAP.keySet()) {
                  Traits traits = Traits.get(key);
                  traitMap.put(key, traits.toTag());
            }

            HashMap<String, String> listMap = new HashMap<>();
            listMap.put("disables_back_slot", Constants.writeList(Constants.DISABLES_BACK_SLOT));
            listMap.put("chestplate_disabled", Constants.writeList(Constants.CHESTPLATE_DISABLED));
            listMap.put("elytra_items", Constants.writeList(Constants.ELYTRA_ITEMS));
            listMap.put("blacklist_items", Constants.writeList(Constants.BLACKLIST_ITEMS));

            // Null Player means data pack is being sent to all players
            if (player == null) {
                  NetworkPackages.S2All(new ConfigureTraits2C(traitMap));
                  NetworkPackages.S2All(new ConfigureLists2C(listMap));
            }
            else {
                  NetworkPackages.S2C(new ConfigureTraits2C(traitMap), player);
                  NetworkPackages.S2C(new ConfigureLists2C(listMap), player);
            }

            String syncedPlayers = player == null ? "all players" : "\"" + player.getDisplayName().getString() + "\"";
            Constants.LOG.info("Syncing {} data to {}", Constants.MOD_ID, syncedPlayers);
      }

      @SubscribeEvent
      public static void loadPlayer(PlayerEvent.StartTracking event) {
            if (event.getEntity() instanceof ServerPlayer thisPlayer && event.getTarget() instanceof Player owner)
                  NetworkPackages.S2C(new SyncBackSlot2C(owner.getUUID(), BackData.get(owner).getStack()), thisPlayer);
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
