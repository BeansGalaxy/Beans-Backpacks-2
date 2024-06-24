package com.beansgalaxy.backpacks.network.serverbound;

import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;

public class InstantPlace implements Packet2S {
      int entityId;
      BlockHitResult blockHitResult;

      public InstantPlace(int entityId, BlockHitResult blockHitResult) {
            this.entityId = entityId;
            this.blockHitResult = blockHitResult;
      }

      public InstantPlace(FriendlyByteBuf byteBuf) {
            this.entityId = byteBuf.readInt();
            this.blockHitResult = entityId == -1 ? byteBuf.readBlockHitResult(): null;
      }

      public static void send(int i, BlockHitResult blockHitResult) {
            new InstantPlace(i, blockHitResult).send2S();
      }

      @Override
      public Network2S getNetwork() {
            return Network2S.INSTANT_PLACE_2S;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(entityId);
            if (entityId == -1)
                  buf.writeBlockHitResult(blockHitResult);
      }

      @Override
      public void handle(ServerPlayer sender) {
            getNetwork().debugMsgDecode();
            if (entityId == -1)
            {
                  BackpackItem.hotkeyOnBlock(sender, blockHitResult.getDirection(), blockHitResult.getBlockPos());
            }
            else if (sender != null && sender.level().getEntity(entityId) instanceof EntityAbstract backpack)
            {
                  backpack.interact(sender);
            }

      }
}
