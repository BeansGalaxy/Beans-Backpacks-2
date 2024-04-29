package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.CommonConfig;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ServerSave extends SavedData {
      public final EnderStorage enderStorage = new EnderStorage();
      public static final CommonConfig CONFIG = new CommonConfig();
      public static final HashMap<Gamerules, Boolean> GAMERULES = Gamerules.mapConfig(CONFIG);

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            enderStorage.toNBT(tag);
            Gamerules.toNBT(tag, GAMERULES, CONFIG);
            return tag;
      }

      public static ServerSave createFromNbt(CompoundTag tag, ServerLevel level) {
            ServerSave save = new ServerSave();
            CONFIG.read();
            save.enderStorage.fromNbt(tag, level);
            GAMERULES.clear();
            GAMERULES.putAll(Gamerules.fromNBT(tag, CONFIG));
            return save;
      }

      public static ServerSave getSave(MinecraftServer server, boolean updateSave) {
            ServerLevel level = server.getLevel(Level.OVERWORLD);
            DimensionDataStorage dataStorage = level.getDataStorage();
            ServerSave save = dataStorage.computeIfAbsent((nbt) -> createFromNbt(nbt, level), ServerSave::new, Constants.MOD_ID);

            if (updateSave)
                  save.setDirty();

            return save;
      }

}
