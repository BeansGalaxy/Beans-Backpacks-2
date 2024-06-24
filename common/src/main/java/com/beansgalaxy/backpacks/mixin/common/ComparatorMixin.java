package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ComparatorBlock.class)
public class ComparatorMixin {
      @Inject(method = "getInputSignal", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
                  at = @At(value = "CONSTANT", args = "intValue=-2147483648", ordinal = 2))
      private void injectBackpackComparatorSignal(Level level, BlockPos $$1, BlockState $$2, CallbackInfoReturnable<Integer> cir, int i, Direction direction, BlockPos blockPos, BlockState $$6, ItemFrame $$7, int j) {
            EntityAbstract backpack = getBackpack(level, direction, blockPos);
            int signal = Integer.MIN_VALUE;
            if (backpack != null)
                  signal = backpack.getAnalogOutput();
            if (signal > j) {
                  if (level instanceof ServerLevel serverLevel) {
                        Player serverPlayer = serverLevel.getPlayerByUUID(backpack.getPlacedBy());
                        Services.REGISTRY.triggerSpecial((ServerPlayer) serverPlayer, SpecialCriterion.Special.COMPARATOR);
                  }
                  cir.setReturnValue(signal);
            }
      }

      @Unique
      private EntityAbstract getBackpack(Level level, Direction direction, BlockPos blockPos) {
            AABB box = new AABB(blockPos.getX(), blockPos.getY() + 2/8f, blockPos.getZ(),
                        blockPos.getX() + 1, blockPos.getY() + 4/8f, blockPos.getZ() + 1);

            List<EntityAbstract> list = level.getEntitiesOfClass(EntityAbstract.class, box, (backpack) -> {
                  if (backpack == null) return false;
                  Direction direction1 = backpack.getDirection();
                  return direction1 == direction;
            });

            return list.size() == 1 ? list.get(0) : null;
      }
}

