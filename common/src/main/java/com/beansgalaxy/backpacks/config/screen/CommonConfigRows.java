package com.beansgalaxy.backpacks.config.screen;

import com.beansgalaxy.backpacks.config.CommonConfig;
import com.beansgalaxy.backpacks.config.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CommonConfigRows extends ConfigRows {
      public CommonConfigRows(ConfigScreen screen, Minecraft minecraft, CommonConfig config) {
            super(screen, minecraft, config);
      }

      @Override
      public List<ConfigLabel> getRows() {
            return List.of(
            new ConfigLabel(Component.literal("Adjust Backpack Sizes")),
                        new IntConfigRow(screen.commonConfig.leather_max_stacks),
                        new IntConfigRow(screen.commonConfig.ender_max_stacks),
                        new IntConfigRow(screen.commonConfig.winged_max_stacks),
                        new IntConfigRow(screen.commonConfig.metal_max_stacks),
                        new IntConfigRow(screen.commonConfig.pot_max_stacks),
                        new IntConfigRow(screen.commonConfig.cauldron_max_buckets),
            new ConfigLabel(Component.literal("Item Whitelists")),
                        new ItemListConfigRow(screen.commonConfig.disable_chestplate),
                        new ItemListConfigRow(screen.commonConfig.disables_back_slot),
                        new ItemListConfigRow(screen.commonConfig.elytra_items),
                        new ItemListConfigRow(screen.commonConfig.blacklist_items),
            new ConfigLabel(Component.literal("Miscellaneous")),
                        new BoolConfigRow(screen.commonConfig.always_disables_back_slot),
                        new MoveBackSlotConfigRow(screen.commonConfig.back_slot_pos)
            );
      }
}
