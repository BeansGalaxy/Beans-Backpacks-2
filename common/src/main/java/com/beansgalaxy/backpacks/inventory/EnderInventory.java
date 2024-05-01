package com.beansgalaxy.backpacks.inventory;

import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderStacks;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class EnderInventory extends BackpackInventory implements EntityAccess {
      public final HashMap<UUID, EnderStorage.Location> locations = new HashMap<>();
      private final UUID uuid;
      private final Level level;
      private final Traits.LocalData traits;
      private CompoundTag trim = new CompoundTag();
      private Component playerName = Component.empty();
      private boolean locked;

      public EnderInventory(UUID uuid, Level level) {
            this.uuid = uuid;
            this.level = level;
            this.traits = new Traits.LocalData("", Kind.ENDER, 0xFFFFFF, null, Component.empty()) {

                  @Override
                  public CompoundTag getTrim() {
                        return trim;
                  }
            };
      }

      private EnderInventory getEnderData() {
            return EnderStorage.getEnderData(this.uuid, this.level);
      }

      private EnderStorage getEnderStorage() {
            return EnderStorage.get(level);
      }

      public EnderInventory setTrim(CompoundTag trim) {
            if (trim != null)
                  this.trim = trim;
            return this;
      }

      public CompoundTag getTrim() {
            if (trim == null)
                  trim = new CompoundTag();
            return trim;
      }

      public Component getPlayerName() {
            return playerName;
      }

      public MutableComponent getPlayerNameColored(RegistryAccess access) {
            Style style = Style.EMPTY;

            if (!trim.isEmpty())
            {
                  ItemStack copy = Services.REGISTRY.getEnder().getDefaultInstance();
                  copy.getOrCreateTag().put("Trim", trim);

                  Optional<ArmorTrim> armorTrim = ArmorTrim.getTrim(access, copy);
                  if (armorTrim.isPresent())
                        style = armorTrim.get().material().value().description().getStyle();
            }

            return playerName.copy().withStyle(style);
      }

      public EnderInventory setItemStacks(NonNullList<ItemStack> stacks) {
            if (stacks == null)
                  return this;
            getItemStacks().clear();
            getItemStacks().addAll(stacks);
            return this;
      }

      public EnderInventory setPlayerName(Component name) {
            if (name != null && !name.equals(ComponentContents.EMPTY)) {
                  playerName = name;
            }
            return this;
      }

      public void setLocked(boolean locked) {
            this.locked = locked;
      }

      public boolean isLocked() {
            return locked;
      }

      public EntityAccess getOwner() {
            return this;
      }

      @Override
      public void playSound(PlaySound sound) {
      }

      @Override
      public Traits.LocalData getTraits() {
            return traits;
      }

      @Override
      public UUID getPlacedBy() {
            return uuid;
      }

      @Override
      public Level level() {
            return level;
      }

      @Override
      public void setChanged() {
            MinecraftServer server = level.getServer();
            if (server != null) {
                  ServerSave save = ServerSave.getSave(server, true);
                  save.enderStorage.forEachViewing(uuid, (viewer) -> SendEnderStacks.send(viewer, uuid));
            }
            super.setChanged();
      }

      @Override
      public int getId() {
            return -1;
      }

      @Override
      public UUID getUUID() {
            return this.uuid;
      }

      @Override
      public BlockPos blockPosition() {
            return new BlockPos(0, 0, 0);
      }

      @Override
      public AABB getBoundingBox() {
            return new AABB(blockPosition(), blockPosition());
      }

      @Override
      public void setLevelCallback(EntityInLevelCallback var1) {

      }

      @Override
      public Stream<? extends EntityAccess> getSelfAndPassengers() {
            return Stream.empty();
      }

      @Override
      public Stream<? extends EntityAccess> getPassengersAndSelf() {
            return Stream.empty();
      }

      @Override
      public void setRemoved(Entity.RemovalReason var1) {

      }

      @Override
      public boolean shouldBeSaved() {
            return false;
      }

      @Override
      public boolean isAlwaysTicking() {
            return false;
      }

      public void flagForUpdate(Level level) {
            int limit = 64;
            for (EnderStorage.Location location : locations.values()) {
                  if (limit == 0) return;
                  level.updateNeighbourForOutputSignal(location.location, Blocks.AIR);
                  limit--;
            }
      }
}
