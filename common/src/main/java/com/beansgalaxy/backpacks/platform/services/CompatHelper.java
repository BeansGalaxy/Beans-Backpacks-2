package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public interface CompatHelper {
      String CURIOS = "curios";
      String TRINKETS = "trinkets";

      boolean isModLoaded(String namespace);

      default boolean anyModsLoaded(String[] namespaces) {
            for (String namespace: namespaces)
                  if (isModLoaded(namespace))
                        return true;
            return false;
      }

      default boolean graveModLoaded() {
            return anyModsLoaded(new String[]{"universal-graves", "yigd", "gravestones"});
      }

      static NonNullList<ItemStack> onDeathForGraveMods(Player player, int x, double y, int z, Direction direction, float yaw) {
            return BackData.get(player).drop(x, y, z, direction, yaw);
      }

      void setBackSlotItem(BackData data, ItemStack stack);

      ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem);

      boolean backSlotDisabled(Player owner);

      CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas);
}
