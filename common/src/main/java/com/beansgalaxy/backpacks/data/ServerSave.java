package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ServerSave extends SavedData {
      public static final HashMap<UUID, EnderStorage.Data> MAPPED_ENDER_DATA = new HashMap<>();
      public static final HashMap<Config, Boolean> CONFIG = new HashMap<>(Config.getDefaults());

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            CompoundTag backpacksSave = new CompoundTag();
            EnderStorage.toNBT(backpacksSave);
            Config.toNBT(backpacksSave);
            tag.put(Constants.MOD_ID, backpacksSave);
            return tag;
      }

      public static ServerSave createFromNbt(CompoundTag tag) {
            ServerSave save = new ServerSave();
            EnderStorage.fromNbtDeprecated(tag);
            CompoundTag backpacksSave;
            if (tag.contains(Constants.MOD_ID)) {
                  backpacksSave = tag.getCompound(Constants.MOD_ID);
                  EnderStorage.fromNbt(backpacksSave);
                  Config.fromNBT(backpacksSave);
            }
            return save;
      }

      public static ServerSave updateSave(MinecraftServer server) {
            DimensionDataStorage dataStorage = server.getLevel(Level.OVERWORLD).getDataStorage();
            ServerSave save = dataStorage.computeIfAbsent(ServerSave::createFromNbt, ServerSave::new,  Constants.MOD_ID);  // search, fallback, MOD_ID
            save.setDirty();

            return save;
      }

}
