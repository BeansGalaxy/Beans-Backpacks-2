package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class EntityEnder extends EntityAbstract {
      private final BackpackInventory inventory = new BackpackInventory() {

            public Entity getOwner() {
                  return EntityEnder.this;
            }

            NonNullList<ServerPlayer> playersViewing = NonNullList.create();

            @Override
            public Viewable getViewable() {
                  return viewable;
            }

            @Override
            public NonNullList<ServerPlayer> getPlayersViewing() {
                  return playersViewing;
            }

            @Override
            public NonNullList<ItemStack> getItemStacks() {
                  return EntityEnder.this.getItemStacks();
            }

            @Override
            public Traits.LocalData getLocalData() {
                  return EntityEnder.this.getLocalData();
            }

            @Override
            public UUID getPlacedBy() {
                  return EntityEnder.this.getPlacedBy();
            }

            @Override
            public void setChanged() {
                  BackpackInventory.super.setChanged();
            }
      };

      public EntityEnder(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      @Override
      public BackpackInventory getInventory() {
            return inventory;
      }

      public EntityEnder(Player player, UUID uuid) {
            super(Services.REGISTRY.getEnderEntity(), player.level());
            entityData.set(PLACED_BY, Optional.of(uuid));
      }

      @Override
      public CompoundTag getTrim() {
            return ServerSave.getEnderData(getPlacedBy(), level()).getTrim();
      }

      @Override
      protected NonNullList<ItemStack> getItemStacks() {
            return ServerSave.getEnderData(getPlacedBy(), level()).getItemStacks();
      }

      @Override @NotNull
      public Component getDisplayName() {
            ServerSave.EnderData enderData = ServerSave.getEnderData(getPlacedBy());
            return enderData.getPlayerName();
      }

      @Override
      public boolean shouldShowName() {
            return true;
      }

      @Override
      public boolean hasCustomName() {
            return true;
      }
}
