package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.general.PlaySound;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Backpack extends Entity {
      public static final EntityDataAccessor<String> KIND = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.STRING);
      public static final EntityDataAccessor<String> KEY = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.STRING);
      public static final EntityDataAccessor<String> NAME = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.STRING);
      public static final EntityDataAccessor<Integer> MAX_STACKS = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.INT);
      public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.INT);
      public static final EntityDataAccessor<CompoundTag> TRIM = SynchedEntityData.defineId(Backpack.class, EntityDataSerializers.COMPOUND_TAG);
      public static int DEFAULT_COLOR = 9062433;

      public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();
      public final BackpackInventory backpackInventory = new BackpackInventory() {

            NonNullList<Player> playersViewing = NonNullList.create();

            @Override
            public Viewable getViewable() {
                  return viewable;
            }

            @Override
            public Kind getKind() {
                  return Backpack.this.getKind();
            }

            @Override
            public int getMaxStacks() {
                  return Backpack.this.entityData.get(MAX_STACKS);
            }

            @Override
            public NonNullList<Player> getPlayersViewing() {
                  return playersViewing;
            }

            @Override
            public NonNullList<ItemStack> getItemStacks() {
                  return Backpack.this.getItemStacks();
            }

            @Override
            public void playSound(PlaySound sound) {
                  sound.at(Backpack.this.getOwner());
            }
      };

      public Backpack(Level $$1, NonNullList<ItemStack> stacks) {
            super(Services.REGISTRY.getEntity() , $$1);
            if (stacks != null && !stacks.isEmpty()) {
                  NonNullList<ItemStack> itemStacks = backpackInventory.getItemStacks();
                  this.backpackInventory.getItemStacks().addAll(itemStacks);
                  itemStacks.clear();
            }
      }

      public Backpack(EntityType<? extends Entity> type, Level level) {
            super(type, level);
      }

      public boolean isMirror() {
            boolean notMirror = this instanceof BackpackEntity;
            return !notMirror;
      }

      public BackpackInventory.Data getData() {
            String name = Backpack.this.entityData.get(NAME);
            int maxStacks = Backpack.this.entityData.get(MAX_STACKS);
            return new BackpackInventory.Data(getKey(), name, getKind(), maxStacks, getColor(), getTrim());
      }

      private final NonNullList<ItemStack> itemStacks = NonNullList.create();

      protected NonNullList<ItemStack> getItemStacks() {
            return this.itemStacks;
      }

      private Entity getOwner() {
            return this;
      }

      public int getColor() {
            int color = this.entityData.get(COLOR);
            return color;
      }

      public Kind getKind() {
            return Kind.fromName(this.entityData.get(KIND));
      }

      public String getKey() {
            return this.entityData.get(KEY);
      }

      public CompoundTag getTrim() {
            return this.entityData.get(TRIM);
      }

      @Override
      protected void defineSynchedData() {
            this.entityData.define(KEY, "");
            this.entityData.define(NAME, "null");
            this.entityData.define(KIND, "");
            this.entityData.define(MAX_STACKS, 0);
            this.entityData.define(COLOR, DEFAULT_COLOR);
            this.entityData.define(TRIM, new CompoundTag());
      }

      public void initDisplay(BackpackInventory.Data data) {
            this.entityData.set(KEY, data.key);
            this.entityData.set(NAME, data.name);
            this.entityData.set(KIND, data.kind.name());
            this.entityData.set(MAX_STACKS, data.maxStacks);
            int color = data.color == BackpackItem.DEFAULT_COLOR ? DEFAULT_COLOR : data.color;
            this.entityData.set(COLOR, color);
            this.entityData.set(TRIM, data.trim);
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag var1) {

      }

      @Override
      protected void addAdditionalSaveData(CompoundTag var1) {

      }

      public void setDisplay(CompoundTag display) {
            this.entityData.set(KEY, display.getString("key"));
            this.entityData.set(NAME, display.getString("name"));
            this.entityData.set(KIND, display.getString("kind"));
            this.entityData.set(MAX_STACKS, display.getInt("max_stacks"));
            this.entityData.set(COLOR, display.getInt("color"));
            this.entityData.set(TRIM, display.getCompound("Trim"));
      }
}
