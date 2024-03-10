package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.data.BackData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
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

      public static Fluid get(ItemStack cauldron) {
            if (!cauldron.hasTag()) return Fluids.EMPTY;

            CompoundTag fluidTag = cauldron.getTagElement("fluid");
            if (fluidTag == null) return Fluids.EMPTY;

            if (fluidTag.contains("id")) {
                  String string = fluidTag.getString("id");
                  return BuiltInRegistries.FLUID.get(new ResourceLocation(string));
            }
            return Fluids.EMPTY;
      }

      public record FluidAttributes(TextureAtlasSprite sprite, Color tint) {

      }
}
