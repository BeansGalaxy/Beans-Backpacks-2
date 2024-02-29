package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ServerSave extends SavedData {

      public static final HashMap<UUID, EnderData> MAPPED_ENDER_DATA = new HashMap<>();

      public static class EnderData {
            private NonNullList<ItemStack> itemStacks = NonNullList.create();
            private CompoundTag trim = new CompoundTag();
            private MutableComponent playerName = Component.empty();
            private HashMap<UUID, BlockPos> locations = new HashMap<>();

            public EnderData(NonNullList<ItemStack> itemStacks, CompoundTag trim, MutableComponent playerName) {
                  this.itemStacks = itemStacks == null ? NonNullList.create() : itemStacks;
                  this.trim = trim == null ? new CompoundTag() : trim;
                  this.playerName = playerName == null ? Component.empty() : playerName;
            }

            public EnderData() {
            }

            public EnderData setTrim(CompoundTag trim) {
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

            public EnderData setItemStacks(NonNullList<ItemStack> stacks) {
                  if (stacks == null)
                        return this;
                  itemStacks.clear();
                  itemStacks.addAll(stacks);
                  return this;
            }

            public EnderData setPlayerName(MutableComponent name) {
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

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            MAPPED_ENDER_DATA.forEach(((uuid, enderData) -> {
                  if (uuid != null && (!enderData.itemStacks.isEmpty() || !enderData.trim.isEmpty() || !enderData.locations.isEmpty())) {
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
                        tag.put(uuid.toString(), data);
                  }
            }));
            return tag;
      }

      public static ServerSave createFromNbt(CompoundTag tag) {
            ServerSave save = new ServerSave();
            for (String key : tag.getAllKeys()) {
                  CompoundTag dataTags = tag.getCompound(key);
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  BackpackInventory.readStackNbt(dataTags, itemStacks);
                  CompoundTag trim = dataTags.getCompound("Trim");
                  MutableComponent playerName = Component.Serializer.fromJson(dataTags.getString("player_name"));
                  EnderData enderData = new EnderData(itemStacks, trim, playerName);

                  CompoundTag locations = dataTags.getCompound("locations");
                  for (String key1 : locations.getAllKeys()) {
                        UUID backpack = UUID.fromString(key1);
                        int[] location = locations.getIntArray(key1);
                        enderData.locations.put(backpack, new BlockPos(location[0], location[1], location[2]));
                  }

                  UUID uuid = UUID.fromString(key);
                  MAPPED_ENDER_DATA.put(uuid, enderData);
            }
            MAPPED_ENDER_DATA.remove(null);
            return save;
      }

      public static ServerSave getServerState(MinecraftServer server) {
            DimensionDataStorage dataStorage = server.getLevel(Level.OVERWORLD).getDataStorage();
            ServerSave save = dataStorage.computeIfAbsent(ServerSave::createFromNbt, ServerSave::new,  Constants.MOD_ID);  // search, fallback, MOD_ID
            save.setDirty();

            return save;
      }

      public static EnderData getEnderData(Player player) {
            return getEnderData(player.getUUID(), player.level());
      }

      public static EnderData getEnderData(UUID uuid, Level level) {
            EnderData enderData = getEnderData(uuid);
            Player player;
            if (uuid != null && (player = level.getPlayerByUUID(uuid)) != null) {
                  MutableComponent copy = player.getName().copy();
                  enderData.setPlayerName(copy);
            }
            return enderData;
      }

      public static CompoundTag getTrim(UUID uuid, Level level) {
            if (uuid == null)
                  return new CompoundTag();

            return getEnderData(uuid, level).getTrim();
      }

      public static EnderData getEnderData(UUID uuid) {
            if (uuid == null) {
                  Constants.LOG.warn("Tried to get UUID of \"null\" --- Returning Empty Ender Data");
                  return new EnderData();
            }
            return MAPPED_ENDER_DATA.computeIfAbsent(uuid, uuid1 -> new EnderData());
      }

      public static void setLocation(UUID owner, UUID backpack, BlockPos location, ServerLevel level) {
            if (owner == null)
                  return;

            EnderData enderData = getEnderData(owner, level);
            enderData.locations.put(backpack, location);
      }

      public static void removeLocation(UUID owner, UUID backpack) {
            EnderData enderData = MAPPED_ENDER_DATA.get(owner);
            if (enderData != null)
                  enderData.locations.remove(backpack);
      }

      public static HashSet<EnderLocation> getLocations(ServerPlayer player) {
            EnderData enderData = getEnderData(player);
            HashSet<EnderLocation> locations = new HashSet<>();
            for (UUID backpacks : enderData.locations.keySet()) {
                  ServerLevel serverLevel = player.serverLevel();
                  Entity entity = serverLevel.getEntity(backpacks);
                  if (entity == null) {
                        BlockPos blockPos = enderData.locations.get(backpacks);
                        locations.add(new EnderLocation(false, blockPos));
                        continue;
                  }

                  if (entity.isRemoved()) {
                        enderData.locations.remove(backpacks);
                        continue;
                  }

                  BlockPos blockPos = entity.blockPosition();
                  enderData.locations.put(backpacks, blockPos);
                  locations.add(new EnderLocation(true, blockPos));
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

      public static class EnderLocation {
            private final boolean isAccurate;
            private final BlockPos location;

            public EnderLocation(boolean accurate, BlockPos location) {
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

            public EnderLocation(FriendlyByteBuf buf) {
                  this.isAccurate = buf.readBoolean();
                  this.location = buf.readBlockPos();
            }
      }
}
