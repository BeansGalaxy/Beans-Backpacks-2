package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class EnderStorage {
      public static void toNBT(@NotNull CompoundTag tag) {
            CompoundTag enderTag = new CompoundTag();

            ServerSave.MAPPED_ENDER_DATA.forEach(((uuid, enderData) -> {
                  if (uuid != null && (!enderData.getItemStacks().isEmpty() || !enderData.getTrim().isEmpty() || !enderData.locations.isEmpty())) {
                        CompoundTag data = new CompoundTag();
                        BackpackInventory.writeNbt(data, enderData.getItemStacks());
                        data.put("Trim", enderData.getTrim());
                        data.putString("player_name", Component.Serializer.toJson(enderData.getPlayerName()));

                        CompoundTag locations = new CompoundTag();
                        for (UUID backpack : enderData.locations.keySet()) {
                              BlockPos blockPos = enderData.locations.get(backpack);
                              int[] pos = {blockPos.getX(), blockPos.getY(), blockPos.getZ()};
                              locations.putIntArray(backpack.toString(), pos);
                        }

                        data.put("locations", locations);
                        enderTag.put(uuid.toString(), data);
                  }
            }));
            tag.put("EnderData", enderTag);
      }

      public static void fromNbt(CompoundTag tag) {
            if (tag.contains("EnderData"))
                  fromNbtDeprecated(tag.getCompound("EnderData"));
      }

      // TODO: MERGE DEPRECATED AFTER FULL RELEASE (20.1-0.14)
      public static void fromNbtDeprecated(CompoundTag tag) {
            for (String key : tag.getAllKeys()) {
                  if (key.equals(Constants.MOD_ID)) // TODO: THIS IF IS ONLY HERE FOR COMPATIBILITY FOR PREVIOUS VERSION
                        continue;
                  CompoundTag dataTags = tag.getCompound(key);
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  BackpackInventory.readStackNbt(dataTags, itemStacks);
                  CompoundTag trim = dataTags.getCompound("Trim");
                  MutableComponent playerName = Component.Serializer.fromJson(dataTags.getString("player_name"));
                  EnderStorage.Data enderData = new EnderStorage.Data(itemStacks, trim, playerName);

                  CompoundTag locations = dataTags.getCompound("locations");
                  for (String key1 : locations.getAllKeys()) {
                        UUID backpack = UUID.fromString(key1);
                        int[] location = locations.getIntArray(key1);
                        enderData.locations.put(backpack, new BlockPos(location[0], location[1], location[2]));
                  }

                  UUID uuid = UUID.fromString(key);
                  ServerSave.MAPPED_ENDER_DATA.put(uuid, enderData);
            }
            ServerSave.MAPPED_ENDER_DATA.remove(null);
      }

      public static Data getEnderData(Player player) {
            return getEnderData(player.getUUID(), player.level());
      }

      public static Data getEnderData(UUID uuid, Level level) {
            if (uuid == null) {
                  Constants.LOG.warn("Tried to get UUID of \"null\" --- Returning Empty Ender Data");
                  return new Data();
            }

            Data enderData = ServerSave.MAPPED_ENDER_DATA.computeIfAbsent(uuid, uuid1 -> new Data());
            Player player = level.getPlayerByUUID(uuid);

            if (player != null)
                  enderData.setPlayerName(player.getName().copy());

            return enderData;
      }

      public static CompoundTag getTrim(UUID uuid, Level level) {
            if (uuid == null)
                  return new CompoundTag();

            return getEnderData(uuid, level).getTrim();
      }

      public static void setLocation(UUID owner, UUID backpack, BlockPos location, ServerLevel level) {
            if (owner == null)
                  return;

            Data enderData = getEnderData(owner, level);
            enderData.locations.put(backpack, location);
      }

      public static void removeLocation(UUID owner, UUID backpack) {
            Data enderData = ServerSave.MAPPED_ENDER_DATA.get(owner);
            if (enderData != null)
                  enderData.locations.remove(backpack);
      }

      public static HashSet<Location> getLocations(ServerPlayer player) {
            Data enderData = getEnderData(player);
            HashSet<Location> locations = new HashSet<>();
            for (UUID backpacks : enderData.locations.keySet()) {
                  ServerLevel serverLevel = player.serverLevel();
                  Entity entity = serverLevel.getEntity(backpacks);
                  if (entity == null) {
                        BlockPos blockPos = enderData.locations.get(backpacks);
                        locations.add(new Location(false, blockPos));
                        continue;
                  }

                  if (entity.isRemoved()) {
                        enderData.locations.remove(backpacks);
                        continue;
                  }

                  BlockPos blockPos = entity.blockPosition();
                  enderData.locations.put(backpacks, blockPos);
                  locations.add(new Location(true, blockPos));
            }
            return locations;
      }

      public static void updateLocations(UUID uuid, Level serverLevel) {
            Player owner = serverLevel.getPlayerByUUID(uuid);
            if (owner instanceof ServerPlayer serverPlayer) {
                  BackData backData = BackData.get(owner);
                  backData.setEnderLocations(getLocations(serverPlayer));
                  Services.NETWORK.sendEnderLocations2C(serverPlayer, backData);
            }
      }

      public static class Data {
            private NonNullList<ItemStack> itemStacks = NonNullList.create();
            private CompoundTag trim = new CompoundTag();
            private MutableComponent playerName = Component.empty();
            private final HashMap<UUID, BlockPos> locations = new HashMap<>();

            public Data(NonNullList<ItemStack> itemStacks, CompoundTag trim, MutableComponent playerName) {
                  this.itemStacks = itemStacks == null ? NonNullList.create() : itemStacks;
                  this.trim = trim == null ? new CompoundTag() : trim;
                  this.playerName = playerName == null ? Component.empty() : playerName;
            }

            public Data() {
            }

            public Data setTrim(CompoundTag trim) {
                  if (trim != null)
                        this.trim = trim;
                  return this;
            }

            public CompoundTag getTrim() {
                  if (trim == null)
                        trim = new CompoundTag();
                  return trim;
            }

            public NonNullList<ItemStack> getItemStacks() {
                  if (itemStacks == null)
                        itemStacks = NonNullList.create();
                  return itemStacks;
            }

            public Data setItemStacks(NonNullList<ItemStack> stacks) {
                  if (stacks == null)
                        return this;
                  itemStacks.clear();
                  itemStacks.addAll(stacks);
                  return this;
            }

            public Data setPlayerName(MutableComponent name) {
                  if (name != null || !name.equals(ComponentContents.EMPTY)) {
                        playerName = name;
                  }
                  return this;
            }

            public MutableComponent getPlayerName() {
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
      }

      public static class Location {
            private final boolean isAccurate;
            private final BlockPos location;

            public Location(boolean accurate, BlockPos location) {
                  this.isAccurate = accurate;
                  this.location = location;
            }

            public MutableComponent toComponent() {
                  int x = location.getX();
                  int y = location.getY();
                  int z = location.getZ();
                  ChatFormatting color = isAccurate ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
                  return Component.literal(x + ", " + y + ", " + z).withStyle(color);
            }

            public void writeBuf(FriendlyByteBuf buf) {
                  buf.writeBoolean(isAccurate);
                  buf.writeBlockPos(location);
            }

            public Location(FriendlyByteBuf buf) {
                  this.isAccurate = buf.readBoolean();
                  this.location = buf.readBlockPos();
            }
      }
}
