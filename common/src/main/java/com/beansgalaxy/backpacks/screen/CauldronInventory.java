package com.beansgalaxy.backpacks.screen;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

public class CauldronInventory {

      public static Fluid interact(ItemStack bucketStack, Fluid fluid) {
            if (fluid.isSame(Fluids.EMPTY))
            {
                  Fluid remove = remove(bucketStack);
                  if (!remove.isSame(Fluids.EMPTY)) {
                        return remove;
                  } else
                        return null;

            }
            else if (add(bucketStack, fluid))
            {
                  return Fluids.EMPTY;
            }
            return null;
      }

      public static boolean add(ItemStack cauldron, Fluid fluid) {
            if (fluid.isSame(Fluids.EMPTY))
                  return false;

            int amount = 1;
            CompoundTag fluidTag = cauldron.getOrCreateTagElement("fluid");
            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Fluid stored = BuiltInRegistries.FLUID.get(new ResourceLocation(string));
                  if (!fluid.isSame(stored))
                        return false;
                  if (fluidTag.contains("amount"))
                        amount += fluidTag.getInt("amount");
            }
            else {
                  ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
                  fluidTag.putString("id", key.toString());
            }

            fluidTag.putInt("amount", amount);
            return true;
      }

      public static Fluid remove(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Fluids.EMPTY;

            CompoundTag fluidTag = cauldron.getTagElement("fluid");
            if (fluidTag == null) return Fluids.EMPTY;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(string));
                  int amount = -1;
                  if (fluidTag.contains("amount"))
                        amount += fluidTag.getInt("amount");

                  if (amount < 1)
                        cauldron.getTag().remove("fluid");
                  else
                        fluidTag.putInt("amount", amount);

                  return fluid;
            }

            return Fluids.EMPTY;
      }

      public record FluidAttributes(TextureAtlasSprite sprite, Color tint) {
      }
}
