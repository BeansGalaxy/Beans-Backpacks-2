package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricConfigHelper implements ConfigHelper {

      @Override
      public Path getPath() {
            return FabricLoader.getInstance().getConfigDir();
      }

}
