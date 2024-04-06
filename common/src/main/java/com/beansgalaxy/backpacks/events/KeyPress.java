package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

public final class KeyPress {
      public static final KeyPress INSTANCE = new KeyPress();

      private KeyPress() {}

      public static final String KEY_CATEGORY = "key.beansbackpacks.category";
      public static final String KEY_BACKPACK_MODIFIER = "key.beansbackpacks.action";
      public static final String KEY_DESCRIPTION = "key.beansbackpacks.description";

      public final KeyMapping ACTION_KEY = new KeyMapping(
                  KEY_BACKPACK_MODIFIER,
                  GLFW.GLFW_KEY_UNKNOWN,
                  KEY_CATEGORY);

      public static void instantPlace(LocalPlayer localPlayer) {
            HitResult hitResult = Minecraft.getInstance().hitResult;

            if (hitResult == null || hitResult.getType() == HitResult.Type.MISS)
                  return;

            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof EntityAbstract entityAbstract) {
                  Services.NETWORK.instantPlace(entityAbstract.getId(), null);
            }

            if (hitResult instanceof BlockHitResult blockHitResult)
            {
                  InteractionResult interactionResult =
                              BackpackItem.hotkeyOnBlock(localPlayer, blockHitResult.getDirection(), blockHitResult.getBlockPos());

                  if (interactionResult.consumesAction())
                        Services.NETWORK.instantPlace(-1, blockHitResult);

            }
      }
}
