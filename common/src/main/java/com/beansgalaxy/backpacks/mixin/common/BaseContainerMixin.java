package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.BaseContainerAccess;
import com.beansgalaxy.backpacks.mixin.common.access.LockCodeAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerMixin extends BlockEntity implements BaseContainerAccess{
      @Shadow private LockCode lockKey;

      public BaseContainerMixin(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
            super($$0, $$1, $$2);
      }

      @Inject(method = "canUnlock", cancellable = true, at = @At("HEAD"))
      private static void unlockedForOwner(Player player, LockCode lockCode, Component component, CallbackInfoReturnable<Boolean> cir) {
            if (!player.isSpectator() && beans_Backpacks_2$isUnlockedForOwner(player, lockCode)) {
                  cir.setReturnValue(true);
            }
      }


      @Override
      public boolean lockContainerForOwner(Player player) {
            String key = ((LockCodeAccess) lockKey).getKey();
            if (key.isEmpty()) {
                  lockKey = new LockCode(beans_Backpacks_2$createLockKeyForOwner(player));
                  saveWithId();
                  return true;
            }
            return false;
      }

      @Unique
      private static boolean beans_Backpacks_2$isUnlockedForOwner(Player player, LockCode lockCode) {
            String key = ((LockCodeAccess) lockCode).getKey();
            String playerKey = beans_Backpacks_2$createLockKeyForOwner(player);
            return Objects.equals(key, playerKey);
      }

      @Unique
      private static String beans_Backpacks_2$createLockKeyForOwner(Player player) {
            return Constants.MOD_ID + player.getUUID();
      }
}
