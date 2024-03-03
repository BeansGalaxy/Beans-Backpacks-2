package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
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
                              CompoundTag location = new CompoundTag();
                              enderData.locations.get(backpack).toNBT(location);
                              locations.put(backpack.toString(), location);
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
                  if (key.equals(Constants.MOD_ID)) // TODO: THIS IS ONLY HERE FOR COMPATIBILITY FOR PREVIOUS VERSION
                        continue;
                  CompoundTag dataTags = tag.getCompound(key);
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  BackpackInventory.readStackNbt(dataTags, itemStacks);
                  CompoundTag trim = dataTags.getCompound("Trim");
                  MutableComponent playerName = Component.Serializer.fromJson(dataTags.getString("player_name"));
                  EnderStorage.Data enderData = new EnderStorage.Data(itemStacks, trim, playerName);

                  CompoundTag locations = dataTags.getCompound("locations");
                  for (String backpack : locations.getAllKeys()) {
                        Location location = new Location(locations.getCompound(backpack));
                        enderData.locations.put(UUID.fromString(backpack), location);
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
            enderData.locations.put(backpack, new Location(location, level.dimension()));
      }

      public static void removeLocation(UUID owner, UUID backpack) {
            Data enderData = ServerSave.MAPPED_ENDER_DATA.get(owner);
            if (enderData != null)
                  enderData.locations.remove(backpack);
      }

      public static void flagForUpdate(EntityEnder ender, MinecraftServer server) {
            UUID placedBy = ender.getPlacedBy();
            if (placedBy == null) return;

            Data data = ServerSave.MAPPED_ENDER_DATA.get(placedBy);
            if (data == null) return;

            int limit = 64;
            for (Location location : data.locations.values()) {
                  if (limit == 0) return;

                  ServerLevel level = server.getLevel(location.dimension);
                  if (level == null) continue;

                  level.updateNeighbourForOutputSignal(location.location, Blocks.AIR);

                  limit--;
            }
      }

      public static HashSet<PackagedLocation> getLocations(UUID owner, MinecraftServer minecraftServer) {
            Data enderData = getEnderData(owner, minecraftServer.getLevel(Level.OVERWORLD));
            HashSet<PackagedLocation> locations = new HashSet<>();
            for (UUID backpack : enderData.locations.keySet()) {

                  Location location = enderData.locations.get(backpack);
                  ResourceKey<Level> dimension = location.dimension;
                  BlockPos blockPos = location.location;

                  ServerLevel level = minecraftServer.getLevel(dimension);
                  if (level == null) {
                        locations.add(new PackagedLocation(false, blockPos, dimension));
                        continue;
                  }

                  Entity entity = level.getEntity(backpack);
                  if (entity == null) {
                        locations.add(new PackagedLocation(false, blockPos, dimension));
                        continue;
                  }

                  if (entity.isRemoved()) {
                        enderData.locations.remove(backpack);
                        continue;
                  }

                  BlockPos newBlockPos = entity.blockPosition();
                  enderData.locations.put(backpack, new Location(newBlockPos, dimension));
                  locations.add(new PackagedLocation(true, newBlockPos, dimension));
            }
            return locations;
      }

      public static void updateLocations(UUID uuid, Level serverLevel) {
            Player owner = serverLevel.getPlayerByUUID(uuid);
            if (owner instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
                  BackData backData = BackData.get(owner);
                  backData.setEnderLocations(getLocations(serverPlayer.getUUID(), serverPlayer.getServer()));
                  Services.NETWORK.sendEnderLocations2C(serverPlayer, backData);
            }
      }

      public static class Data {
            private NonNullList<ItemStack> itemStacks = NonNullList.create();
            private CompoundTag trim = new CompoundTag();
            private MutableComponent playerName = Component.empty();
            private final HashMap<UUID, Location> locations = new HashMap<>();

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
            private final BlockPos location;
            private final ResourceKey<Level> dimension;

            public Location(BlockPos location, ResourceKey<Level> dimension) {
                  this.location = location;
                  this.dimension = dimension;
            }

            public void toNBT(CompoundTag tag) {
                  int[] pos = {location.getX(), location.getY(), location.getZ()};
                  tag.putIntArray("BlockPos", pos);
                  ResourceLocation dimension = this.dimension.location();
                  tag.putString("namespace", dimension.getNamespace());
                  tag.putString("path", dimension.getPath());

            }

            public Location(CompoundTag tag) {
                  int[] pos = tag.getIntArray("BlockPos");
                  this.location = new BlockPos(pos[0], pos[1], pos[2]);
                  ResourceLocation r = new ResourceLocation(tag.getString("namespace"), tag.getString("path"));
                  this.dimension = ResourceKey.create(Registries.DIMENSION, r);
            }
      }

      public static class PackagedLocation {
            private final boolean isAccurate;
            private final BlockPos location;
            private final ResourceLocation dimension;

            public PackagedLocation(boolean accurate, BlockPos location, ResourceKey<Level> dimension) {
                  this.isAccurate = accurate;
                  this.location = location;
                  this.dimension = dimension.location();
            }

            public MutableComponent toComponent() {
                  MutableComponent literal = Component.literal("");

                  int x = location.getX();
                  formatCord(x, literal);
                  literal.append(", ");
                  int y = location.getY();
                  formatCord(y, literal);
                  literal.append(", ");
                  int z = location.getZ();
                  formatCord(z, literal);

                  literal.append(" " + dimension.toShortLanguageKey());

                  ChatFormatting color = isAccurate ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
                  return literal.withStyle(color);
            }

            private static void formatCord(int cord, MutableComponent literal) {
                  String number = String.valueOf(cord);
                  int length = number.length();
                  while (length < 3) {
                        literal.append(" ");
                        length++;
                  }
                  literal.append(number);
            }

            public void writeBuf(FriendlyByteBuf buf) {
                  buf.writeBoolean(isAccurate);
                  buf.writeBlockPos(location);
                  buf.writeResourceLocation(dimension);
            }

            public PackagedLocation(FriendlyByteBuf buf) {
                  this.isAccurate = buf.readBoolean();
                  this.location = buf.readBlockPos();
                  this.dimension = buf.readResourceLocation();
            }
      }
}
