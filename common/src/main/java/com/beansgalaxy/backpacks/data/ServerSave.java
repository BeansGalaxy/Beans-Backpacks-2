package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ServerSave extends SavedData {
      public static final EnderStorage ENDER_STORAGE = new EnderStorage();
      public static final HashMap<Gamerules, Boolean> CONFIG = new HashMap<>(Gamerules.getDefaults());

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            ENDER_STORAGE.toNBT(tag);
            Gamerules.toNBT(tag, CONFIG);
            return tag;
      }

      public static ServerSave createFromNbt(CompoundTag tag) {
            ServerSave save = new ServerSave();
            boolean isOldSave = EnderStorage.get().fromNbtDeprecated(tag);
            if (!isOldSave) {
                  EnderStorage.get().fromNbt(tag);
                  Gamerules.fromNBT(tag);
            }
            return save;
      }

      public static ServerSave getSave(MinecraftServer server, boolean updateSave) {
            DimensionDataStorage dataStorage = server.getLevel(Level.OVERWORLD).getDataStorage();
            ServerSave save = dataStorage.computeIfAbsent(ServerSave::createFromNbt, ServerSave::new,  Constants.MOD_ID);

            if (updateSave)
                  save.setDirty();

            return save;
      }

}
