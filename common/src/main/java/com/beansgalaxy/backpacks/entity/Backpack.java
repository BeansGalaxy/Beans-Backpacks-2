package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Backpack extends Entity {
      public static final EntityDataAccessor<String> KEY = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.STRING);
      public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.INT);
      public static final EntityDataAccessor<CompoundTag> TRIM = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPOUND_TAG);
      public static final EntityDataAccessor<Component> HOVER_NAME = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPONENT);
      public static final int DEFAULT_COLOR = 9062433;

      public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();
      public final BackpackInventory backpackInventory = new BackpackInventory() {

            public Entity getOwner() {
                  return Backpack.this;
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
                  return Backpack.this.getItemStacks();
            }

            @Override
            public Traits.LocalData getLocalData() {
                  return Backpack.this.getLocalData();
            }
      };

      public Backpack(Level $$1) {
            super(Services.REGISTRY.getEntity() , $$1);
      }

      public Backpack(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public Traits.LocalData getLocalData() {
            return new Traits.LocalData(entityData.get(KEY), entityData.get(COLOR), entityData.get(TRIM), entityData.get(HOVER_NAME), getDamage());
      }

      public boolean isMirror() {
            boolean notMirror = this instanceof BackpackEntity;
            return !notMirror;
      }

      public BackpackInventory getInventory() {
            return backpackInventory;
      }

      private final NonNullList<ItemStack> itemStacks = NonNullList.create();

      protected NonNullList<ItemStack> getItemStacks() {
            return this.itemStacks;
      }

      public int getColor() {
            int color = this.entityData.get(COLOR);
            return color;
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

      public int getDamage() {
            return 0;
      }

      @Override
      protected void defineSynchedData() {
            this.entityData.define(KEY, "");
            this.entityData.define(COLOR, DEFAULT_COLOR);
            this.entityData.define(TRIM, new CompoundTag());
            this.entityData.define(HOVER_NAME, Component.empty());
      }

      public void initDisplay(Traits.LocalData data) {
            this.entityData.set(KEY, data.key);
            this.entityData.set(COLOR, data.color);
            this.entityData.set(TRIM, data.trim);
            this.entityData.set(HOVER_NAME, data.hoverName);
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
            backpackInventory.writeNbt(tag);
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag tag) {
            backpackInventory.readStackNbt(tag);
      }

      public void setDisplay(CompoundTag display) {
            this.entityData.set(KEY, display.getString("key"));
            this.entityData.set(COLOR, display.getInt("color"));
            this.entityData.set(TRIM, display.getCompound("Trim"));
            MutableComponent hoverName = Component.Serializer.fromJson(display.getString("hover_name"));
            if (hoverName != null && !hoverName.toString().equals("empty"))
                  this.entityData.set(HOVER_NAME, hoverName);

      }
}
