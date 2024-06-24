package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.network.serverbound.InstantPlace;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public final class KeyPress {
      public static final KeyPress INSTANCE = new KeyPress();

      private KeyPress() {}

      public static final String KEY_CATEGORY = "key.beansbackpacks.category";
      public static final String ACTION_KEY_IDENTIFIER = "key.beansbackpacks.action";
      public static final String MENUS_KEY_IDENTIFIER = "key.beansbackpacks.inventory";
      public static final String ACTION_KEY_DESC = "key.beansbackpacks.desc.action";
      public static final String MENUS_KEY_DESC = "key.beansbackpacks.desc.inventory";

      public final KeyMapping ACTION_KEY = new KeyMapping(
                  ACTION_KEY_IDENTIFIER,
                  GLFW.GLFW_KEY_UNKNOWN,
                  KEY_CATEGORY);

      public final KeyMapping MENUS_KEY = new KeyMapping(
                  MENUS_KEY_IDENTIFIER,
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

      public static KeyMapping getDefaultKeyBind() {
            Minecraft instance = Minecraft.getInstance();
            if (Constants.CLIENT_CONFIG.sneak_default.get())
                  return instance.options.keyShift;

            return instance.options.keySprint;
      }

      public static KeyMapping getActionKeyBind() {
            KeyMapping sprintKey = getDefaultKeyBind();
            KeyMapping customKey = INSTANCE.ACTION_KEY;

            return customKey.isUnbound() ? sprintKey : customKey;
      }

      public static KeyMapping getMenusKeyBind() {
            KeyMapping sprintKey = getActionKeyBind();
            KeyMapping customKey = INSTANCE.MENUS_KEY;

            return customKey.isUnbound() ? sprintKey : customKey;
      }

      public static Component getKeyReadable(KeyMapping keyBind) {
            String name = "tldr." + keyBind.saveString();
            if (Language.getInstance().has(name)) {
                  return Component.translatable(name);
            }

            return keyBind.getTranslatedKeyMessage();
      }

      public static Component getReadable(boolean inMenu) {
            KeyMapping keyBind = inMenu ? getMenusKeyBind() : getActionKeyBind();
            InputConstants.Key key = InputConstants.getKey(keyBind.saveString());
            Component readable = getKeyReadable(keyBind);
            if (InputConstants.Type.MOUSE.equals(key.getType())) {
                  return Component.translatable("help.beansbackpacks.mouse_button", readable);
            }
            Boolean instantPlace = Constants.CLIENT_CONFIG.instant_place.get();
            if (instantPlace) {
                  return Component.translatable("help.beansbackpacks.instant_place", readable);
            }
            if (inMenu)
                  return Component.translatable("help.beansbackpacks.menu_hotkey", readable);

            return Component.translatable("help.beansbackpacks.action_hotkey", readable, getKeyReadable(Minecraft.getInstance().options.keyUse));
      }

      public static @NotNull isPressed isPressed(Minecraft minecraft, KeyMapping bind) {
            KeyMapping sneakKey = minecraft.options.keyShift;
            if (sneakKey.same(bind))
                  sneakKey.setDown(bind.isDown());

            InputConstants.Key key = InputConstants.getKey(bind.saveString());
            long window = minecraft.getWindow().getWindow();
            int value = key.getValue();

            boolean isMouseKey = key.getType().equals(InputConstants.Type.MOUSE);
            boolean isPressed = isMouseKey ? GLFW.glfwGetMouseButton(window, value) == 1 : InputConstants.isKeyDown(window, value);
            return new isPressed(isMouseKey, isPressed);
      }

      public record isPressed(boolean onMouse, boolean pressed) {
      }
}
