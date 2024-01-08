package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public class PlaceBackpackEvent {
    private static NonNullList<CoyoteClick> coyoteList = NonNullList.create();

    public static InteractionResult interact(Player player, InteractionHand hand, Direction direction, BlockPos clickedPos) {
        if (player.isSpectator())
            return InteractionResult.PASS;

        if (BackSlot.get(player).actionKeyPressed && Kind.isBackpack(BackSlot.get(player).getItem())) {
            if (!player.isSprinting() && !player.isSwimming()) {
                return BackpackItem.hotkeyOnBlock(player, direction, clickedPos);
            }
            else if (player.level() instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                if (coyoteList.stream().noneMatch(clicks -> clicks.player.equals(player)))
                    coyoteList.add(new CoyoteClick(player, direction, clickedPos, hand));
            }
        }
        return InteractionResult.PASS;
    }

    public static void cancelCoyoteClick(Player player, InteractionResult actionResult, boolean useItem) {
        if (coyoteList.isEmpty())
            return;

        coyoteList.removeIf(clicks -> {
            if (!clicks.player.equals(player))
                return false;

            if (useItem)
                clicks.successItem = actionResult;
            else
                clicks.successBlock = actionResult;

            if (clicks.hasNull())
                return false;

            if (clicks.success())
                return true;

            if (InteractionResult.SUCCESS != BackpackItem.hotkeyOnBlock(player, clicks.direction, clicks.blockPos))
                return false;

            player.swing(InteractionHand.MAIN_HAND, true);
            return true;
        });
    }

    static class CoyoteClick {
        private final Player player;
        private final Direction direction;
        private final BlockPos blockPos;
        private final boolean handIsEmpty;
        private InteractionResult successItem = null;
        private InteractionResult successBlock = null;
        public int time = 15;

        CoyoteClick(Player player, Direction direction, BlockPos blockPos, InteractionHand hand) {
            this.player = player;
            this.direction = direction;
            this.blockPos = blockPos;
            this.handIsEmpty = player.getItemInHand(hand).isEmpty();
            if (direction.getAxis().isHorizontal())
                time += 5;
        }

        boolean hasNull() {
            if (handIsEmpty)
                return successBlock == null;

            return successItem == null || successBlock == null;
        }

        boolean success() {
            if (handIsEmpty)
                return successBlock.consumesAction();

            return successItem.consumesAction() || successBlock.consumesAction();
        }
    }
}
