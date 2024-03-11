package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

public class CauldronInventory {

      public static Item interact(ItemStack bucketStack, Item bucket) {
            if (bucket instanceof BucketItemAccess access && access.beans_Backpacks_2$getFluid().isSame(Fluids.EMPTY))
            {
                  Item remove = remove(bucketStack);
                  if (!remove.equals(Items.AIR)) {
                        return remove;
                  } else
                        return null;

            }
            else if (add(bucketStack, bucket))
            {
                  return Items.AIR;
            }
            return null;
      }

      public static boolean add(ItemStack cauldron, Item bucket) {
            if (bucket instanceof BucketItem || bucket instanceof SolidBucketItem) {
                  int amount = 1;
                  CompoundTag fluidTag = cauldron.getOrCreateTagElement("bucket");
                  if (fluidTag.contains("id")) {
                        String string = fluidTag.getString("id");
                        Item stored = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                        if (!bucket.equals(stored))
                              return false;
                        if (fluidTag.contains("amount"))
                              amount += fluidTag.getInt("amount");
                  } else {
                        ResourceLocation key = BuiltInRegistries.ITEM.getKey(bucket);
                        fluidTag.putString("id", key.toString());
                  }

                  fluidTag.putInt("amount", amount);
                  return true;
            }
            return false;
      }

      public static Item remove(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("bucket");
            if (fluidTag == null) return Items.AIR;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Item fluid = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  int amount = -1;
                  if (fluidTag.contains("amount"))
                        amount += fluidTag.getInt("amount");

                  if (amount < 1)
                        cauldron.getTag().remove("bucket");
                  else
                        fluidTag.putInt("amount", amount);

                  return fluid;
            }

            return Items.AIR;
      }

      public static Item getBucket(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("bucket");
            if (fluidTag == null) return Items.AIR;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  return BuiltInRegistries.ITEM.get(new ResourceLocation(string));
            }
            return Items.AIR;
      }

      public static boolean addSolid(ItemStack cauldron, BlockState blockState) {
            Block block = blockState.getBlock();
            if (block.equals(Blocks.AIR))
                  return false;

            CompoundTag fluidTag = cauldron.getTagElement("fluid");
            if (fluidTag != null)
                  return false;

            int amount = 1;
            CompoundTag blockTag = cauldron.getOrCreateTagElement("block");
            if (blockTag.contains("id")) {
                  String string = blockTag.getString("id");
                  Block stored = BuiltInRegistries.BLOCK.get(new ResourceLocation(string));
                  if (!block.equals(stored)) return false;
                  if (blockTag.contains("amount"))
                        amount += blockTag.getInt("amount");
            }
            else {
                  ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
                  blockTag.putString("id", key.toString());
            }

            blockTag.putInt("amount", amount);
            return true;
      }

      public record FluidAttributes(TextureAtlasSprite sprite, Color tint) {

      }
}
