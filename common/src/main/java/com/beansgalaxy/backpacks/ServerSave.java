package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ServerSave extends SavedData {

      public static final HashMap<UUID, EnderData> MAPPED_ENDER_DATA = new HashMap<>();

      public static class EnderData {
            private NonNullList<ItemStack> itemStacks = NonNullList.create();
            private CompoundTag trim = new CompoundTag();
            private MutableComponent playerName = Component.empty();

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

            public MutableComponent getPlayerName() {
                  return playerName;
            }

            public EnderData setPlayerName(MutableComponent name) {
                  if (!name.equals(ComponentContents.EMPTY)) {
                        playerName = name;
                  }
                  return this;
            }
      }

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            MAPPED_ENDER_DATA.forEach(((uuid, enderData) -> {
                  if (uuid != null && (!enderData.itemStacks.isEmpty() || !enderData.trim.isEmpty())) {
                        CompoundTag data = new CompoundTag();
                        BackpackInventory.writeNbt(data, enderData.getItemStacks());
                        data.put("Trim", enderData.getTrim());
                        data.putString("player_name", Component.Serializer.toJson(enderData.getPlayerName()));
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
                  UUID uuid = UUID.fromString(key);
                  if (!enderData.itemStacks.isEmpty() && !enderData.trim.isEmpty())
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

      public static EnderData getEnderData(UUID uuid) {
            if (uuid == null) {
                  Constants.LOG.warn("Tried to get UUID of \"null\" --- Returning Empty Ender Data");
                  return new EnderData();
            }
            return MAPPED_ENDER_DATA.computeIfAbsent(uuid, uuid1 -> new EnderData());
      }
}
