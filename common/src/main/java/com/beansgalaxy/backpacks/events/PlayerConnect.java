package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.entity.Data;
import net.minecraft.nbt.CompoundTag;

public class PlayerConnect {
      public static void onConnect(CompoundTag tag) {
            String key = tag.getString("key");
            Data data = new Data(tag.getCompound("data"));
            Constants.REGISTERED_DATA.put(key, data);
      }
}
