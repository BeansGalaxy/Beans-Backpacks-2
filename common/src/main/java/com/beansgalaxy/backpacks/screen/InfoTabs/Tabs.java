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
import java.util.List;

public enum Tabs {
      BACKPACK(1, 0x19EE5500, new InfoTab() {
            public ItemStack getDisplay() {
                  return Services.REGISTRY.getLeather().getDefaultInstance();
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return KeyPress.getReadable(line < 10);
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:beansbackpacks");
            }

            public int getX() {
                  return -23;
            }
      },
      ENDER(    2, 0x108800FF, new InfoTab() {
            public ItemStack getDisplay() {
                  return Services.REGISTRY.getEnder().getDefaultInstance();
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/ender_backpacks");
            }

            public int getX() {
                  return -46;
            }
      },
      POT(3, 0x19DDCC00, new InfoTab() {
            public ItemStack getDisplay() {
                  return Items.DECORATED_POT.getDefaultInstance();
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/decorated_pots");
            }

            public int getX() {
                  return -69;
            }
      },
      CAULDRON(4, 0x120055FF, new InfoTab() {
            public ItemStack getDisplay() {
                  return Items.CAULDRON.getDefaultInstance();
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return KeyPress.getReadable(line < 7);
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/fluid_cauldrons");
            }

            public int getX() {
                  return -92;
            }

            public int getIconX() {
                  return getX() + 1;
            }

            public int getIconY() {
                  return getY() - 1;
            }
      },
      NULL(-1, 0x1000FF00, new InfoTab() {
            public ItemStack getDisplay() {
                  return Constants.createLabeledBackpack("null");
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return minecraft.player.getName().plainCopy().withStyle(ChatFormatting.BLACK);
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/thank_you");
            }

            public int getX() {
                  return -150;
            }

            public int getY() {
                  return 144 + 16;
            }

            public int getIconY() {
                  return getY() + 1;
            }
      },
      BUNDLE(-2, 0x23d95842, new InfoTab() {
            public ItemStack getDisplay() {
                  return Services.REGISTRY.getBigBundle().getDefaultInstance();
            }

            public Component getVariableForLine(Minecraft minecraft, int line) {
                  return minecraft.player.getName().plainCopy().withStyle(ChatFormatting.BLACK);
            }
      }) {
            public boolean isUnlocked(Minecraft minecraft) {
                  return getAdvancement(minecraft.getConnection(), "beansbackpacks:info/super_special_player");
            }

            public int getX() {
                  boolean unlocked = NULL.isUnlocked(Minecraft.getInstance());
                  return -150 + (unlocked ? 23 : 0);
            }

            public int getY() {
                  return 144 + 16;
            }

            public int getIconY() {
                  return getY() + 1;
            }
      };

      final int index;
      final Color color;
      private final InfoTab tab;

      Tabs(int index, int color, InfoTab tab) {
            this.index = index;
            this.color = new Color(color, true);
            this.tab = tab;
      }

      public int getX() {
            return 0;
      }

      public int getY() {
            return 0;
      }

      public int getIconX() {
            return getX();
      }

      public int getIconY() {
            return getY();
      }

      public boolean isUnlocked(Minecraft minecraft) {
            return false;
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
            gui.renderFakeItem(tab.getDisplay(), getIconX() + x + 23, getIconY() + y);
      }

      public Component getVarForLine(Minecraft minecraft, int line) {
            return tab.getVariableForLine(minecraft, line).plainCopy().withStyle(ChatFormatting.BLACK);
      }

      public int getTexX() {
            return index * 24;
      }

      private interface InfoTab {

            ItemStack getDisplay();

            default Component getVariableForLine(Minecraft minecraft, int line) {
                  return Component.empty();
            }
      }
}
