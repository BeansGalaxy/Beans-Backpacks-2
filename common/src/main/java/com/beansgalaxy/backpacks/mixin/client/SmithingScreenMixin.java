package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.entity.Backpack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                  public Traits.LocalData getLocalData() {
                        ItemStack stack = smithingMenu.getSlot(3).getItem();
                        return Traits.LocalData.fromStack(stack);
                  }
            };
      }

      @Redirect(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(Lnet/minecraft/client/gui/GuiGraphics;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/world/entity/LivingEntity;)V"))
      protected void renderEntityRedirect(GuiGraphics graphics, int x, int y, int scale, Quaternionf $$4, Quaternionf $$5, LivingEntity livingEntity) {
            boolean switchRender = false;


            if (smithingMenu != null) {
                  boolean backpack = Kind.isBackpack(smithingMenu.getSlot(3).getItem());
                  if (backpack) {
                        boolean stack = backpackPreview.getLocalData().maxStacks() != 0;
                        switchRender = stack;
                  }
            }

            Entity renderedEntity = switchRender ? backpackPreview : livingEntity;
            RendererHelper.renderBackpackForSmithing(graphics, x, y, scale, $$5, switchRender, renderedEntity);
      }

}
