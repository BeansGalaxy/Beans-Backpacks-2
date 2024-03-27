package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.access.BucketItemAccess;
import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.inventory.SpecialTooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public abstract class ClientSpecialTooltip implements ClientTooltipComponent {
      public final int amount;

      public ClientSpecialTooltip(int amount) {
            this.amount = amount;
      }

      @Override
      public int getHeight() {
            return 7;
      }

      @Override
      public int getWidth(@NotNull Font font) {
            return 19 + font.width("x" + this.amount);
      }

      private int getAmountOffset(Font font) {
            int offset = -font.width(String.valueOf(this.amount));
            return offset + getAmountOverflow(font);
      }

      private int getAmountOverflow(Font font) {
            if (this.amount > 999) {
                  int floor = Mth.floor(this.amount / 1000f);
                  return font.width(String.valueOf(floor));
            }
            return 0;
      }

      public static class Pot extends ClientSpecialTooltip {
            private final Item item;

            public Pot(SpecialTooltip specialTooltip) {
                  super(specialTooltip.amount);
                  this.item = specialTooltip.item;
            }

            @Override
            public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics gui) {
                  gui.drawString(font, "x" + this.amount, x + 19, y + 3, 0xFFFFFFFF);
                  gui.renderFakeItem(item.getDefaultInstance(), x, y - 1);
            }
      }

      public static class Cauldron extends ClientSpecialTooltip {
            private final Item item;
            private final TextureAtlas blockAtlas;

            public Cauldron(SpecialTooltip specialTooltip) {
                  super(specialTooltip.amount);
                  this.item = specialTooltip.item;
                  this.blockAtlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            }

            @Override
            public void renderImage(Font font, int x, int y, GuiGraphics gui) {
                  if (item instanceof BucketsAccess access) {
                        int h = -1;
                        if (item instanceof BucketItemAccess bucket) {
                              Fluid fluid = bucket.beans_Backpacks_2$getFluid();
                              CauldronInventory.FluidAttributes attributes = Services.COMPAT.getFluidTexture(fluid, blockAtlas);
                              Color tint = attributes.tint();
                              gui.blit(x, y + h, 16, 16, 16, attributes.sprite(), tint.getRed() / 255f, tint.getGreen() / 255f, tint.getBlue() / 255f, 1);
                        } else {
                              Optional<BlockState> optional = access.getBlockState();
                              if (optional.isPresent()) {
                                    Block block = optional.get().getBlock();
                                    BlockState blockState = block.defaultBlockState();
                                    TextureAtlasSprite blockTexture = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getParticleIcon();
                                    gui.blit(x, y + h, 16, 16, 16, blockTexture, 1, 1, 1, 1);
                              } else {
                                    gui.renderFakeItem(item.getDefaultInstance(), x, y + h);
                              }
                        }
                        int scale = access.fullScale();
                        int count = amount / scale;
                        int remainder = amount % scale;
                        StringBuilder builder = new StringBuilder().append("x").append(count);
                        int amountLineY = y + 4 + h;
                        if (remainder != 0) {
                              gui.drawString(font, "+" + remainder, x + 19, amountLineY + 4, 0xFFFFFFFF);
                              amountLineY -= 4;
                        }

                        gui.drawString(font, builder.toString(), x + 19, amountLineY, 0xFFFFFFFF);
                  }
            }
      }
}
