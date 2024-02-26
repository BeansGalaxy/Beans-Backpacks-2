package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin extends ItemCombinerScreen<SmithingMenu> {
      @Unique public SmithingMenu smithingMenu;
      @Unique private Backpack backpackPreview;

      public SmithingScreenMixin(SmithingMenu $$0, Inventory $$1, Component $$2, ResourceLocation $$3, SmithingMenu menu) {
            super($$0, $$1, $$2, $$3);
      }

      @Inject(method = "<init>", at = @At("TAIL"))
      private void init(SmithingMenu $$0, Inventory $$1, Component $$2, CallbackInfo ci) {
            this.smithingMenu = $$0;
      }

      @Inject(method = "subInit", at = @At("TAIL"))
      protected void subInit(CallbackInfo ci) {
            this.backpackPreview = new Backpack(this.minecraft.level) {
                  @Override
                  public UUID getPlacedBy() {
                        return SmithingScreenMixin.this.minecraft.player.getUUID();
                  }

                  @Override
                  public CompoundTag getTrim() {
                        ItemStack stack = smithingMenu.getSlot(3).getItem();
                        CompoundTag tag = stack.getTag();
                        if (tag == null || !tag.contains("Trim"))
                              return new CompoundTag();

                        return tag.getCompound("Trim");
                  }

                  @Override
                  public Traits.LocalData getLocalData() {
                        ItemStack stack = smithingMenu.getSlot(3).getItem();
                        return Traits.LocalData.fromstack(stack);
                  }
            };
      }

      @Inject(method = "renderBg", cancellable = true, at = @At(value = "HEAD"))
      protected void smithingCompatInject(GuiGraphics graphics, float f, int i1, int i2, CallbackInfo ci) {
            if (Services.COMPAT.isModLoaded("bettersmithingtable") && beans_Backpacks_2$doesRenderBackpack(graphics, leftPos + 111, topPos + 65)) {
                  super.renderBg(graphics, f, i1, i2);
                  ci.cancel();
            }
      }

      @Inject(method = "renderBg", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(Lnet/minecraft/client/gui/GuiGraphics;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/world/entity/LivingEntity;)V"))
      protected void renderEntityRedirect(GuiGraphics graphics, float $$1, int $$2, int $$3, CallbackInfo ci) {
            if (beans_Backpacks_2$doesRenderBackpack(graphics, leftPos + 142, topPos + 75))
                  ci.cancel();
      }

      @Unique
      private boolean beans_Backpacks_2$doesRenderBackpack(GuiGraphics graphics, int x, int y) {
            if (smithingMenu != null
                        && Kind.isBackpack(smithingMenu.getSlot(3).getItem())
                        && backpackPreview.getLocalData().maxStacks() != 0)
            {
                  RendererHelper.renderBackpackForSmithing(graphics, (float) x, (float) y, backpackPreview);
                  return true;
            }
            return false;
      }
}
