package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.data.*;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class EntityEnder extends EntityAbstract {
      public EntityEnder(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public EntityEnder(Level level) {
            super(Services.REGISTRY.getEnderEntity(), level);
      }

      private EnderStorage getEnderStorage() {
            return EnderStorage.get(level());
      }

      public Optional<EnderInventory> getEnderData() {
            return entityData.get(OWNER).map(in ->
                        EnderStorage.getEnderData(in, level()));
      }

      @Override
      protected boolean isLocked() {
            return getEnderData().map(EnderInventory::isLocked).orElse(true);
      }

      @Override
      public BackpackInventory getInventory() {
            return getEnderData().orElse(null);
      }

      @Override
      public Traits.LocalData getTraits() {
            if (traits.isEmpty())
                  traits = new Traits.LocalData(this.entityData.get(LOCAL_DATA)) {

                        @Override
                        public CompoundTag getTrim() {
                              return getEnderData().map(EnderInventory::getTrim).orElse(new CompoundTag());
                        }

                  };
            return traits;
      }

      @Override @NotNull
      public Component getDisplayName() {
            return getEnderData().map(EnderInventory::getPlayerName).orElse(Component.empty());
      }

      @Override
      public UUID getPlacedBy() {
            Optional<UUID> uuid = entityData.get(OWNER);
            return uuid.orElse(null);
      }

      @Override
      public void kill() {
            getEnderData().ifPresent(in -> in.locations.remove(getUUID()));
            super.kill();
            level().updateNeighbourForOutputSignal(pos, Blocks.AIR);
      }

      @Override
      public void remove(RemovalReason $$0) {
            UUID placedBy = getPlacedBy();
            if (placedBy != null)
                  getEnderStorage().removeViewer(placedBy, this);

            super.remove($$0);
      }

      @Override
      public boolean shouldShowName() {
            return true;
      }

      @Override
      public boolean hasCustomName() {
            return getPlacedBy() != null;
      }

      @Override
      public void setPlacedBy(Optional<UUID> placedBy) {
            placedBy.ifPresent(in -> {
                  entityData.set(OWNER, placedBy);
                  if (!level().isClientSide) {
                        EnderStorage.getEnderData(in, level()).locations.put(in,
                                    new EnderStorage.Location(blockPosition(), this.level().dimension()));

                        getEnderStorage().addViewer(in, this);
                  }
            });
      }

      @Override
      protected void reapplyPosition() {
            super.reapplyPosition();
            level().updateNeighbourForOutputSignal(pos, Blocks.AIR);

            getEnderStorage().addViewer(getPlacedBy(), this);
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag tag) {
            fromNBT(tag);
            getEnderStorage().addViewer(getPlacedBy(), this);
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
            toNBT(tag);
      }

      @Override
      public int getAnalogOutput() {
            if (isRemoved() || getPlacedBy() == null)
                  return 0;

            return super.getAnalogOutput();
      }
}
