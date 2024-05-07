package com.beansgalaxy.backpacks.screen.InfoTabs;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;

public enum Tabs {
      BACKPACK(1, new int[]{-23, 0}, 0x19EE5500, new InfoTab() {
            public ItemStack getDisplay() {
                  return Services.REGISTRY.getLeather().getDefaultInstance();
            }

            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:beansbackpacks");
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return KeyPress.getReadable(line < 10);
            }
      }),
      ENDER(    2, new int[]{-46,  0}, 0x108800FF, new InfoTab() {
            public ItemStack getDisplay() {
                  return Services.REGISTRY.getEnder().getDefaultInstance();
            }

            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/ender_backpacks");
            }
      }),
      POT(3, new int[]{-69,  0}, 0x19DDCC00, new InfoTab() {
            public ItemStack getDisplay() {
                  return Items.DECORATED_POT.getDefaultInstance();
            }

            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/decorated_pots");
            }
      }),
      CAULDRON(4, new int[]{-92, -1}, 0x120055FF, new InfoTab() {
            public ItemStack getDisplay() {
                  return Items.CAULDRON.getDefaultInstance();
            }

            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/fluid_cauldrons");
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return KeyPress.getReadable(line < 7);
            }
      }),
      NULL(5, new int[]{-114, 0}, 0x1000FF00, new InfoTab() {
            public ItemStack getDisplay() {
                  return Constants.createLabeledBackpack("null");
            }

            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/thank_you");
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return minecraft.player.getName().plainCopy().withStyle(ChatFormatting.BLACK);
            }
      });

      final int index;
      final int[] offsetXY;
      final Color color;
      private final InfoTab tab;

      Tabs(int index, int[] offsetXY, int color, InfoTab tab) {
            this.index = index;
            this.offsetXY = offsetXY;
            this.color = new Color(color, true);
            this.tab = tab;
      }

      public boolean isUnlocked(Minecraft minecraft) {
            return tab.isUnlocked(minecraft);
      }

      public static boolean getAdvancement(ClientPacketListener connection, String... locations) {
            if (connection == null)
                  return false;

            for (String location : locations) {
                  ResourceLocation resourceLocation = ResourceLocation.tryParse(location);
                  if (resourceLocation != null && connection.getAdvancements().getAdvancements().get(resourceLocation) != null)
                        return true;
            }
            return false;
      }

      public void render(GuiGraphics gui, int x, int y) {
            gui.renderFakeItem(tab.getDisplay(), x + offsetXY[0] - offsetXY[1] + 23, y + offsetXY[1]);
      }

      public Component getVarForLine(Minecraft minecraft, int line) {
            return tab.getVariableForLine(minecraft, line).plainCopy().withStyle(ChatFormatting.BLACK);
      }

      private interface InfoTab {

            ItemStack getDisplay();

            boolean isUnlocked(Minecraft minecraft);

            default Component getVariableForLine(Minecraft minecraft, int line) {
                  return Component.empty();
            }
      }
}
