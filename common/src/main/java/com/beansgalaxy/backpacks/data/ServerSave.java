package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.CommonConfig;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ServerSave extends SavedData {
      public static final CommonConfig CONFIG = new CommonConfig();
      public static final HashMap<Gamerules, Boolean> GAMERULES = Gamerules.mapConfig(CONFIG);
      public final EnderStorage enderStorage = new EnderStorage();
      public final HashSet<UUID> heldLockedAdvancement = new HashSet<>();
      public final HashSet<GameProfile> superSpecialPlayers = new HashSet<>();

      @Override @NotNull
      public CompoundTag save(@NotNull CompoundTag tag) {
            enderStorage.toNBT(tag);
            Gamerules.toNBT(tag, GAMERULES, CONFIG);
            tag.putString("LockedAdvancement", lockedAdvancementToString());
            return tag;
      }

      @NotNull
      private String lockedAdvancementToString() {
            StringBuilder sb = new StringBuilder();
            Iterator<UUID> iterator = heldLockedAdvancement.iterator();
            while (iterator.hasNext()) {
                  UUID next = iterator.next();
                  sb.append(next.toString());
                  if (iterator.hasNext())
                        sb.append(',');
            }
            return sb.toString();
      }

      public static ServerSave createFromNbt(CompoundTag tag, ServerLevel level) {
            ServerSave save = new ServerSave();
            CONFIG.read();
            save.enderStorage.fromNbt(tag, level);
            GAMERULES.clear();
            GAMERULES.putAll(Gamerules.fromNBT(tag, CONFIG));
            decodeLockedAdvancement(tag, save);
            save.loadSuperSpecialPlayers();
            return save;
      }

      private static void decodeLockedAdvancement(CompoundTag tag, ServerSave save) {
            String string = tag.getString("LockedAdvancement");
            String[] split = string.split(",");
            for (String uuid : split)
                  if (!Constants.isEmpty(uuid))
                        save.heldLockedAdvancement.add(UUID.fromString(uuid));
      }

      public static ServerSave getSave(MinecraftServer server, boolean updateSave) {
            ServerLevel level = server.getLevel(Level.OVERWORLD);
            DimensionDataStorage dataStorage = level.getDataStorage();
            ServerSave save = dataStorage.computeIfAbsent((nbt) -> createFromNbt(nbt, level), ServerSave::new, Constants.MOD_ID);

            if (updateSave)
                  save.setDirty();

            return save;
      }

      public void grantLockedAchievement(MinecraftServer server, UUID uuid) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) {
                  heldLockedAdvancement.add(uuid);
                  setDirty();
            } else
                  Services.REGISTRY.triggerSpecial(player, SpecialCriterion.Special.LOCKED);
      }

      public void loadSuperSpecialPlayers() {
            try {
                  URL url = new URL("https://raw.githubusercontent.com/BeansGalaxy/Beans-Backpacks-2/common/super_special_players.txt");
                  Scanner scanner = new Scanner(url.openStream());
                  while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (Constants.isEmpty(line)) continue;

                        String[] split = line.split(":");
                        UUID uuid = UUID.fromString(split[1]);
                        GameProfile e = new GameProfile(uuid, split[0]);
                        superSpecialPlayers.add(e);
                  }
            } catch (IOException e) {
                  throw new RuntimeException(e);
            }
      }

      public boolean isSuperSpecial(Player player) {
            GameProfile gameProfile = player.getGameProfile();
            for (GameProfile specialPlayer : superSpecialPlayers) {
                  if (gameProfile.equals(specialPlayer))
                        return true;
            }
            return false;
      }
}
