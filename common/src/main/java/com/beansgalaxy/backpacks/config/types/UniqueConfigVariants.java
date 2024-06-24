package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.StringJoiner;

public interface UniqueConfigVariants {
      static HSetConfigVariant<Item> itemList(String name, String... defau) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            for (String entry : defau) stringJoiner.add(entry);

            String string = stringJoiner.toString();
            HSetConfigVariant.Builder<Item> configBuilder = HSetConfigVariant.Builder.create(
                        /* ENCODE */   Constants::itemShortString,
                        /* DECODE */   in -> BuiltInRegistries.ITEM.get(new ResourceLocation(in)));
            return configBuilder
                        .isValid(in -> BuiltInRegistries.ITEM.containsKey(new ResourceLocation(in)))
                        .defauString(string)
                        .build(name);
      }
}
