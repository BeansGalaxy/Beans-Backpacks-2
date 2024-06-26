package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.nio.file.Path;

public interface ConfigHelper {

      static boolean keepBackSlot(Level level) {
            boolean keepBackSlot = ServerSave.GAMERULES.get(Gamerules.KEEP_BACK_SLOT);
            return level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || keepBackSlot;
      }

      Path getPath();

}
