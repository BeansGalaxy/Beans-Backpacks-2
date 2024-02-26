package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.FabricMain;
import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.entity.BackpackMenu;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket2S;
import com.beansgalaxy.backpacks.network.packages.SyncBackInventory2C;
import com.beansgalaxy.backpacks.network.packages.SyncBackSlot2All;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FabricNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            SprintKeyPacket2S.C2S(sprintKeyPressed);
      }

      @Override
      public void SyncViewers(Entity owner, byte viewers) {
            Level world = owner.level();
            List<ServerPlayer> playerList = world.getServer().getPlayerList().getPlayers();
            for (ServerPlayer serverPlayer : playerList)
                  if (serverPlayer.level().dimension() == world.dimension()) {
                        FriendlyByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(owner.getId());
                        buf.writeByte(viewers);

                        ServerPlayNetworking.send(serverPlayer, NetworkPackages.SYNC_VIEWERS_2All, buf);
                  }
      }

      @Override
      public void openBackpackMenu(Player viewer, BackpackEntity owner) {
                  viewer.openMenu(owner.getInventory().getMenuProvider());
      }

      @Override
      public void openBackpackMenu(Player viewer, Player owner) {
                  viewer.openMenu(BackData.get(owner).backpackInventory.getMenuProvider());

      }

      @Override
      public MenuProvider getMenuProvider(Entity entity) {
            return new ExtendedScreenHandlerFactory() {

                  @Override
                  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeInt(entity.getId());
                  }

                  @Override
                  public Component getDisplayName() {
                        return Component.literal("");
                  }

                  @Nullable
                  @Override
                  public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                        if (player.isSpectator())
                              return null;
                        else {
                              BackpackInventory backpackInventory = BackpackInventory.get(entity);
                              if (player instanceof ServerPlayer serverPlayer)
                                    backpackInventory.addViewer(serverPlayer);
                              return new BackpackMenu(containerId, player.getInventory(), backpackInventory);
                        }
                  }
            };
      }

      @Override
      public void SyncBackSlot(ServerPlayer owner) {
            SyncBackSlot2All.S2All(owner, BackData.get(owner).getStack());
      }

      @Override
      public void backpackInventory2C(ServerPlayer owner) {
            SyncBackInventory2C.S2C(owner);
      }

      @Override
      public void instantPlace(int i, BlockHitResult blockHitResult) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(i);
            if (blockHitResult != null)
                  buf.writeBlockHitResult(blockHitResult);
            ClientPlayNetworking.send(NetworkPackages.INSTANT_PLACE_2S, buf);
      }

      @Override
      public void pickFromBackpack2S(int slot) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(slot);
            ClientPlayNetworking.send(NetworkPackages.PICK_BACKPACK_2S, buf);
      }

      @Override
      public void sendEnderData2C(ServerPlayer player, UUID uuid) {
            ServerSave.EnderData enderData = ServerSave.getEnderData(uuid, player.level());
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUUID(uuid);
            buf.writeNbt(enderData.getTrim());

            CompoundTag tag = new CompoundTag();
            BackpackInventory.writeNbt(tag, enderData.getItemStacks());
            buf.writeNbt(tag);
            String string = Component.Serializer.toJson(enderData.getPlayerName());
            buf.writeUtf(string);

            ServerPlayNetworking.send(player, FabricMain.INITIAL_SYNC, buf);
      }
}
