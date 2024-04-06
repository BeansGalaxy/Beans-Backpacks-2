package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.data.config.Config;
import com.beansgalaxy.backpacks.data.config.MenuVisibility;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.InfoWidget;

import java.util.HashSet;

import static com.beansgalaxy.backpacks.config.ClientConfig.*;
import static com.beansgalaxy.backpacks.config.CommonConfig.*;

public class ForgeConfigHelper implements ConfigHelper {
      @Override
      public int getIntConfig(Config config) {
            switch (config) {
                  case LEATHER_MAX_STACKS ->  {
                        return LEATHER_MAX_STACKS.get();
                  }
                  case WINGED_MAX_STACKS ->  {
                        return WINGED_MAX_STACKS.get();
                  }
                  case ENDER_MAX_STACKS ->  {
                        return ENDER_MAX_STACKS.get();
                  }
                  case METAL_MAX_STACKS ->  {
                        return METAL_MAX_STACKS.get();
                  }
                  case CAULDRON_MAX_BUCKETS ->  {
                        return CAULDRON_MAX_STACKS.get();
                  }
                  case POT_MAX_STACKS -> {
                        return POT_MAX_STACKS.get();
                  }
            }
            return config.get(Integer.class);
      }

      @Override
      public boolean getBoolConfig(Config config) {
            switch (config) {
                  case UNBIND_ENDER_ON_DEATH -> {
                        return UNBIND_ENDER_ON_DEATH.get();
                  }
                  case LOCK_ENDER_OFFLINE -> {
                        return LOCK_ENDER_OFFLINE.get();
                  }
                  case LOCK_BACKPACK_OFFLINE -> {
                        return LOCK_BACKPACK_OFFLINE.get();
                  }
                  case LOCK_BACKPACK_NOT_OWNER -> {
                        return LOCK_BACKPACK_NOT_OWNER.get();
                  }
                  case KEEP_BACK_SLOT -> {
                        return KEEP_BACK_SLOT.get();
                  }
            }
            return config.get(Boolean.class);
      }

      @Override
      public MenuVisibility getMenuVisibility() {
            return MENU_VISIBILITY.get();
      }

      @Override
      public HashSet<InfoWidget.Tab> getHiddenTabs() {
            return new HashSet<>(HIDDEN_TABS.get());
      }

      @Override
      public void saveHiddenTabs(HashSet<InfoWidget.Tab> hiddenTabs) {
            HIDDEN_TABS.set(hiddenTabs.stream().toList());
            HIDDEN_TABS.save();
      }
}
