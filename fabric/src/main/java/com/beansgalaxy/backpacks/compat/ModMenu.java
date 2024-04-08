package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenu implements ModMenuApi {

      @Override
      public ConfigScreenFactory<?> getModConfigScreenFactory() {
            if (Services.COMPAT.isModLoaded(CompatHelper.CLOTH_CONFIG))
                  return screen -> AutoConfig.getConfigScreen(FabricConfig.class, screen).get();
            return screen -> null;
      }

}
