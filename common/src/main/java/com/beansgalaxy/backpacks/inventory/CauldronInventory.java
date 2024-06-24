package com.beansgalaxy.backpacks.inventory;

import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.awt.*;

public class CauldronInventory {
      public static int getMaxSize() {
            return Traits.get("CAULDRON").getMaxStacks() * 4;
      }

      public static int sizeLeft(ItemStack cauldron) {
            int maxSize = getMaxSize();
            if (!cauldron.hasTag()) return maxSize;

            CompoundTag fluidTag = cauldron.getTagElement("back_slot");
            if (fluidTag == null) return maxSize;

            if (fluidTag.contains("amount")) {
                  return maxSize - fluidTag.getInt("amount");
            }
            return maxSize;
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

            CompoundTag fluidTag = cauldron.getOrCreateTagElement("back_slot");
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

            if (amount > getMaxSize()) return null;
            fluidTag.putInt("amount", amount);
            return access.getEmptyInstance();
      }

      public static Item add(ItemStack cauldron, BucketLikeAccess access) {
            int amount = access.scale();
            CompoundTag fluidTag = cauldron.getOrCreateTagElement("back_slot");
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

            if (amount > getMaxSize()) return null;
            fluidTag.putInt("amount", amount);
            return access.getEmptyInstance();

      }

      public static Item remove(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("back_slot");
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
                        else if (amount == 0) {
                              cauldron.removeTagKey("back_slot");
                        }
                        else
                              fluidTag.putInt("amount", amount);

                        return fluid;
                  }
            }
            return Items.AIR;
      }

      public static Item remove(ItemStack cauldron, BucketLikeAccess likeAccess) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("back_slot");
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
                              cauldron.removeTagKey("back_slot");
                        else
                              fluidTag.putInt("amount", amount);

                        return fluid;
                  }
            }
            return Items.AIR;
      }

      public static Item getBucket(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Items.AIR;

            CompoundTag fluidTag = cauldron.getTagElement("back_slot");
            if (fluidTag == null) return Items.AIR;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  return BuiltInRegistries.ITEM.get(new ResourceLocation(string));
            }
            return Items.AIR;
      }

      public record Attributes(Item bucket, BucketsAccess access, int amount, int amountToPlace) {
            public static Attributes create(ItemStack cauldron) {
                  if (!cauldron.hasTag()) return null;

                  CompoundTag fluidTag = cauldron.getTagElement("back_slot");
                  if (fluidTag == null) return null;

                  if (fluidTag.contains("id") && fluidTag.contains("amount")) {
                        String string = fluidTag.getString("id");
                        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                        int amount = fluidTag.getInt("amount");
                        int amountToPlace = item instanceof BucketLikeAccess access ? access.fullScale() : 4;
                        if (item instanceof BucketsAccess access)
                              return new Attributes(item, access, amount, amountToPlace);
                  }
                  return null;
            }
      }

      public static ItemStack quickInsert(ItemStack backStack, ItemStack stack, Level level) {
            Item bucket = stack.getItem();
            BucketsAccess access = null;
            BucketLikeAccess bucketLikeAccess = null;
            if (bucket instanceof BlockItem blockItem && blockItem.getBlock() instanceof BucketLikeAccess blockAccess)
                  bucketLikeAccess = blockAccess;
            else if (bucket instanceof BucketLikeAccess blockAccess)
                  bucketLikeAccess = blockAccess;

            Item add = null;
            if (bucketLikeAccess != null) {
                  add = add(backStack, bucketLikeAccess);
                  access = bucketLikeAccess;
            } else if (bucket instanceof BucketsAccess bucketsAccess) {
                  add = add(backStack, bucket);
                  access = bucketsAccess;
            }

            if (add != null) {
                  if (level.isClientSide())
                        Tooltip.playSound(access.defaultPlaceSound(), 1, 0.4f);
                  stack.shrink(1);
                  return add.getDefaultInstance();
            }
            return ItemStack.EMPTY;
      }

      public record FluidAttributes(TextureAtlasSprite sprite, Color tint) {

      }
}
