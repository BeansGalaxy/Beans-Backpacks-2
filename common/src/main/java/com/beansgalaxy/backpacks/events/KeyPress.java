package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
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

      public static final String KEY_BACKPACK_MODIFIER = "key.beansbackpacks.action";
      public static final String KEY_DESCRIPTION = "key.beansbackpacks.description";

      public final KeyMapping ACTION_KEY = new KeyMapping(
                  KEY_BACKPACK_MODIFIER,
                  GLFW.GLFW_KEY_UNKNOWN,
                  KeyMapping.CATEGORY_GAMEPLAY);

      public static void instantPlace(LocalPlayer localPlayer) {
            HitResult hitResult = Minecraft.getInstance().hitResult;

            if (hitResult == null || hitResult.getType() == HitResult.Type.MISS)
                  return;

            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof BackpackEntity backpackEntity)
            {
                  InteractionResult interact = backpackEntity.interact(localPlayer);
                  if (interact.consumesAction())
                        Services.NETWORK.instantPlace(backpackEntity.getId(), null);
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
