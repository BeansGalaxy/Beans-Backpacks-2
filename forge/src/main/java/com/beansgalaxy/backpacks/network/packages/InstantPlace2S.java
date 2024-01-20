package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InstantPlace2S {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(InstantPlace2S.class, i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(InstantPlace2S::encode).decoder(InstantPlace2S::new).consumerMainThread(InstantPlace2S::handle).add();
      }

      int entityId;
      BlockHitResult blockHitResult;

      public InstantPlace2S(int entityId, BlockHitResult blockHitResult) {
            this.entityId = entityId;
            this.blockHitResult = blockHitResult;
      }

      public InstantPlace2S(FriendlyByteBuf byteBuf) {
            this.entityId = byteBuf.readInt();
            this.blockHitResult = entityId == -1 ? byteBuf.readBlockHitResult(): null;

      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(entityId);
            if (entityId == -1)
                  buf.writeBlockHitResult(blockHitResult);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            ServerPlayer sender = context.get().getSender();
            if (entityId == -1)
            {
                  BackpackItem.hotkeyOnBlock(sender, blockHitResult.getDirection(), blockHitResult.getBlockPos());
            }
            else if (sender != null && sender.level().getEntity(entityId) instanceof BackpackEntity backpack)
            {
                  backpack.interact(sender);
            }

      }
}
