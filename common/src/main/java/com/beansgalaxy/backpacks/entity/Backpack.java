package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class Backpack extends Entity {
      public static final EntityDataAccessor<Optional<UUID>> PLACED_BY = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.OPTIONAL_UUID);
      public static final EntityDataAccessor<CompoundTag> LOCAL_DATA = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPOUND_TAG);
      public static final int DEFAULT_COLOR = 9062433;

      public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();
      protected Traits.LocalData traits = Traits.LocalData.EMPTY;

      public Backpack(Level $$1) {
            super(Services.REGISTRY.getGeneralEntity(), $$1);
      }

      public Backpack(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public Traits.LocalData getTraits() {
            if (traits.isEmpty())
                  traits = new Traits.LocalData(this.entityData.get(LOCAL_DATA));
            return traits;
      }

      public BackpackInventory.Viewable getViewable() {
            return viewable;
      }

      public UUID getPlacedBy() {
            Optional<UUID> uuid = entityData.get(PLACED_BY);
            return uuid.orElseGet(() -> this.uuid);
      }

      @Override
      public Direction getDirection() {
            return Direction.UP;
      }

      @Override
      protected void defineSynchedData() {
            this.entityData.define(LOCAL_DATA, Traits.LocalData.EMPTY.toNBT());
            this.entityData.define(PLACED_BY, Optional.empty());
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag compoundTag) {
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag compoundTag) {
      }
}
