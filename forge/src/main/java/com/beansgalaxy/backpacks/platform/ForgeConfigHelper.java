package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgeConfigHelper implements ConfigHelper {

      @Override
      public Path getPath() {
            return FMLPaths.CONFIGDIR.get();
      }

}
