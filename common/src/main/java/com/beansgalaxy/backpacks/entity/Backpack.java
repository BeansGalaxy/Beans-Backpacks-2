package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class Backpack extends Entity {
      public static final EntityDataAccessor<String> KEY = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.STRING);
      public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.INT);
      public static final EntityDataAccessor<CompoundTag> TRIM = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPOUND_TAG);
      public static final EntityDataAccessor<Component> HOVER_NAME = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPONENT);
      public static final EntityDataAccessor<Optional<UUID>> PLACED_BY = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.OPTIONAL_UUID);
      public static final int DEFAULT_COLOR = 9062433;

      public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();

      public Backpack(Level $$1) {
            super(Services.REGISTRY.getGeneralEntity(), $$1);
      }

      public Backpack(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public Traits.LocalData getLocalData() {
            return new Traits.LocalData(getKey(), getColor(), getTrim(), getCustomName(), getDamage());
      }

      public BackpackInventory.Viewable getViewable() {
            return viewable;
      }

      public boolean isMirror() {
            boolean notMirror = this instanceof EntityAbstract;
            return !notMirror;
      }

      public int getColor() {
            return this.entityData.get(COLOR);
      }

      public Kind getKind() {
            String key = getKey();
            return Traits.get(key).kind;
      }

      public String getKey() {
            return this.entityData.get(KEY);
      }

      public CompoundTag getTrim() {
            return this.entityData.get(TRIM);
      }

      public UUID getPlacedBy() {
            Optional<UUID> uuid = entityData.get(PLACED_BY);
            return uuid.orElseGet(() -> this.uuid);
      }

      public int getDamage() {
            return 0;
      }

      @Override
      protected void defineSynchedData() {
            this.entityData.define(KEY, "");
            this.entityData.define(COLOR, DEFAULT_COLOR);
            this.entityData.define(TRIM, new CompoundTag());
            this.entityData.define(HOVER_NAME, Component.empty());
            this.entityData.define(PLACED_BY, Optional.empty());
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag compoundTag) {
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag compoundTag) {
      }
}
