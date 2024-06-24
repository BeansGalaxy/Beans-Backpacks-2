package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.config.ConfigScreen;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.network.chat.Component;

public class ModMenu implements ModMenuApi {

      @Override
      public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return ConfigScreen::new;
      }

}
