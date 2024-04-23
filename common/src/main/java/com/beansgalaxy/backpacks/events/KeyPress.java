package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.serverbound.InstantPlace;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
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

            ItemStack stack = BackData.get(localPlayer).getStack();
            if (!stack.isEmpty() && !Kind.isBackpack(stack)) {
                  UseKeyEvent.tryUseCauldron(localPlayer, hitResult);
                  return;
            }

            if (hitResult == null || hitResult.getType() == HitResult.Type.MISS)
                  return;

            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof EntityAbstract entityAbstract) {
                  InstantPlace.send(entityAbstract.getId(), null);
            }

            if (hitResult instanceof BlockHitResult blockHitResult)
            {

                  InteractionResult interactionResult =
                              BackpackItem.hotkeyOnBlock(localPlayer, blockHitResult.getDirection(), blockHitResult.getBlockPos());

                  if (interactionResult.consumesAction())
                        InstantPlace.send(-1, blockHitResult);

            }
      }
}
