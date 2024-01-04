package com.beansgalaxy.backpacks.events;

import net.minecraft.client.KeyMapping;
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

}
