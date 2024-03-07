package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Config;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

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
                  if (EntityEnder.this.level() instanceof ServerLevel serverLevel)
                        EnderStorage.flagForUpdate(EntityEnder.this, serverLevel.getServer());

                  BackpackInventory.super.setChanged();
            }
      };

      public EntityEnder(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public EntityEnder(Player player, Optional<UUID> uuid) {
            super(Services.REGISTRY.getEnderEntity(), player.level());
            entityData.set(PLACED_BY, uuid);

            if (level() instanceof ServerLevel serverLevel)
                  EnderStorage.setLocation(getPlacedBy(), this.uuid, blockPosition(), serverLevel);
      }

      @Override
      public BackpackInventory getInventory() {
            return inventory;
      }

      @Override
      public CompoundTag getTrim() {
            return EnderStorage.getTrim(getPlacedBy(), level());
      }

      @Override
      protected NonNullList<ItemStack> getItemStacks() {
            return EnderStorage.getEnderData(getPlacedBy(), level()).getItemStacks();
      }

      @Override @NotNull
      public Component getDisplayName() {
            EnderStorage.Data enderData = EnderStorage.getEnderData(getPlacedBy(), level());
            return enderData.getPlayerName();
      }

      @Override
      public UUID getPlacedBy() {
            Optional<UUID> uuid = entityData.get(PLACED_BY);
            return uuid.orElse(null);
      }

      @Override
      public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
            UUID placedBy;
            if ((placedBy = getPlacedBy()) != null && (ServerSave.CONFIG.get(Config.ENDER_LOCK_LOGGED_OFF) && level().getPlayerByUUID(placedBy) == null)) {
                  PlaySound.HIT.at(this, this.getKind());
                  this.hop(.1);
                  return InteractionResult.SUCCESS;
            }

            return super.interact(player, hand);
      }

      @Override
      public void kill() {
            EnderStorage.removeLocation(getPlacedBy(), getUUID());
            super.kill();
            level().updateNeighbourForOutputSignal(pos, Blocks.AIR);
      }

      @Override
      public boolean shouldShowName() {
            return true;
      }

      @Override
      public boolean hasCustomName() {
            return getPlacedBy() != null;
      }

      public void setPlacedBy(Optional<UUID> uuid) {
            entityData.set(PLACED_BY, uuid);
      }

      @Override
      protected void reapplyPosition() {
            super.reapplyPosition();
            level().updateNeighbourForOutputSignal(pos, Blocks.AIR);
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag tag) {
            this.setDirection(Direction.from3DDataValue(tag.getByte("facing")));
            this.setDisplay(tag.getCompound("display"));
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
            tag.putByte("facing", (byte)this.direction.get3DDataValue());
            tag.put("display", getDisplay());
      }

      @Override
      public int getAnalogOutput() {
            if (isRemoved() || getPlacedBy() == null)
                  return 0;

            return super.getAnalogOutput();
      }
}
