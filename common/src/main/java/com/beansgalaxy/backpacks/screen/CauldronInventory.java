package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.awt.*;

public class CauldronInventory {
      public static final int MAX_SIZE = 24 * 4;

      public static int sizeLeft(ItemStack cauldron) {
            if (!cauldron.hasTag()) return MAX_SIZE;

            CompoundTag fluidTag = cauldron.getTagElement("bucket");
            if (fluidTag == null) return MAX_SIZE;

            if (fluidTag.contains("amount")) {
                  return MAX_SIZE - fluidTag.getInt("amount");
            }
            return MAX_SIZE;
      }

      public static Item add(ItemStack cauldron, Item bucket) {
            int amount = 0;
            BucketsAccess access = null;
            if (bucket instanceof BucketsAccess item) {
                  access = item;
                  amount = access.scale();
            }
            else if (bucket instanceof BlockItem blockItem
            && blockItem.getBlock() instanceof BucketLikeAccess block) {
                  access = block;
                  bucket = block.getFilledInstance();
                  amount = block.scale();
            }

            if (access == null) return null;

            CompoundTag fluidTag = cauldron.getOrCreateTagElement("bucket");
            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Item stored = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  if (!bucket.equals(stored))
                        return null;
                  if (fluidTag.contains("amount"))
                        amount += fluidTag.getInt("amount");
            } else {
                  ResourceLocation key = BuiltInRegistries.ITEM.getKey(bucket);
                  fluidTag.putString("id", key.toString());
            }

            fluidTag.putInt("amount", amount);
            return access.getEmptyInstance();
      }

      public static Item add(ItemStack cauldron, BucketLikeAccess access) {
            int amount = access.scale();
            CompoundTag fluidTag = cauldron.getOrCreateTagElement("bucket");
            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Item stored = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  if (!access.getFilledInstance().equals(stored))
                        return null;
                  if (fluidTag.contains("amount"))
                        amount += fluidTag.getInt("amount");
            } else {
                  ResourceLocation key = BuiltInRegistries.ITEM.getKey(access.getFilledInstance());
                  fluidTag.putString("id", key.toString());
            }

            fluidTag.putInt("amount", amount);
            return access.getEmptyInstance();

      }

      public static Item remove(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("bucket");
            if (fluidTag == null) return Items.AIR;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Item fluid = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  if (fluid instanceof BucketsAccess access) {
                        int amount = -access.scale();
                        if (fluidTag.contains("amount"))
                              amount += fluidTag.getInt("amount");

                        if (amount < 0)
                              return Items.AIR;
                        else if (amount == 0)
                              cauldron.getTag().remove("bucket");
                        else
                              fluidTag.putInt("amount", amount);

                        return fluid;
                  }
            }
            return Items.AIR;
      }

      public static Item remove(ItemStack cauldron, BucketLikeAccess likeAccess) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("bucket");
            if (fluidTag == null) return Items.AIR;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  Item fluid = BuiltInRegistries.ITEM.get(new ResourceLocation(string)).asItem();
                  if (fluid instanceof BucketLikeAccess access && access.getFilledInstance().equals(likeAccess.getFilledInstance())) {
                        int amount = -likeAccess.fullScale();
                        if (fluidTag.contains("amount"))
                              amount += fluidTag.getInt("amount");

                        if (amount < 0)
                              return Items.AIR;
                        else if (amount == 0)
                              cauldron.getTag().remove("bucket");
                        else
                              fluidTag.putInt("amount", amount);

                        return fluid;
                  }
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

      public record FluidAttributes(TextureAtlasSprite sprite, Color tint) {

      }
}
