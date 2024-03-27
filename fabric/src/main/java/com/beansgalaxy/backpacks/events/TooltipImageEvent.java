package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.client.renderer.ClientSpecialTooltip;
import com.beansgalaxy.backpacks.inventory.SpecialTooltip;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class TooltipImageEvent implements TooltipComponentCallback {
      @Override
      public @Nullable ClientTooltipComponent getComponent(TooltipComponent component) {
            if (component instanceof SpecialTooltip specialTooltip) {
                  if (component instanceof SpecialTooltip.Pot) {
                        return new ClientSpecialTooltip.Pot(specialTooltip);
                  }
                  if (component instanceof SpecialTooltip.Cauldron) {
                        return new ClientSpecialTooltip.Cauldron(specialTooltip);

                  }
            }
            return null;
      }
}
