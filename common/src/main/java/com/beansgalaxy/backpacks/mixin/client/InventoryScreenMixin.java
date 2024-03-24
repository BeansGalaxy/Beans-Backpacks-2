package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.access.ClickAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements ClickAccessor {
      @Unique private static final ResourceLocation INFO_BUTTON_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/gui/info_tab.png");
      @Shadow @Final private RecipeBookComponent recipeBookComponent;
      @Shadow private boolean buttonClicked;
      @Shadow private boolean widthTooNarrow;
      @Unique private final CyclingSlotBackground backSlotIcon = new CyclingSlotBackground(BackData.get(Minecraft.getInstance().player).backSlot.slotIndex);

      public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
            super(screenHandler, playerInventory, text);
      }

      @Inject(method = "containerTick", at = @At("HEAD"))
      public void containerTick(CallbackInfo ci) {
            this.backSlotIcon.tick(BackSlot.getTextures());
      }

      @Inject(method = "renderBg", at = @At("TAIL"))
      protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
            BackSlot backSlot = BackData.get(this.minecraft.player).backSlot;
            if (backSlot.isActive() && backSlot.slotIndex != -1)
                  this.backSlotIcon.render(this.menu, context, delta, this.leftPos, this.topPos);
            if (!recipeBookComponent.isVisible() && infoWidget.isFocused())
                  infoWidget.render(context, mouseX, mouseY, delta);
      }

      @Override
      public void beans_Backpacks_2$instantPlace() {
            Slot hoveredSlot = this.hoveredSlot;
            if (hoveredSlot == null)
                  return;

            BackData backData = BackData.get(this.minecraft.player);
            if (hoveredSlot.getItem() == backData.getStack() && backData.backpackInventory.isEmpty())
                  return;

            this.slotClicked(hoveredSlot, hoveredSlot.index, 0, ClickType.PICKUP);
      }

      @Unique private final InfoWidget infoWidget = new InfoWidget();
      @Inject(method = "init", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                  target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;setInitialFocus(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V"))
      public void backpackHelpWidget(CallbackInfo ci) {
            infoWidget.init(this.width, this.height, this.leftPos, this.topPos, this.minecraft, this.menu, () -> this.buttonClicked = true);
            this.addWidget(this.infoWidget);
            this.addRenderableWidget(infoWidget.homeButton);
            for (InfoWidget.InfoButton button : infoWidget.buttons)
                  this.addRenderableWidget(button);
      }

      @Inject(method = "mouseClicked", at = @At("RETURN"))
      public void hideHelpWidget(double $$0, double $$1, int $$2, CallbackInfoReturnable<Boolean> cir) {
            infoWidget.homeButton.setVisible(!this.recipeBookComponent.isVisible() && !infoWidget.isFocused());
      }
}
