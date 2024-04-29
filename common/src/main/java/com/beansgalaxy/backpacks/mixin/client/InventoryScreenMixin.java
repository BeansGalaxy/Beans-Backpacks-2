package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.inventory.BackpackTooltip;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.InfoWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu>{
      @Shadow @Final private RecipeBookComponent recipeBookComponent;
      @Shadow private boolean buttonClicked;
      @Unique private final CyclingSlotBackground backSlotIcon = new CyclingSlotBackground(BackData.get(Minecraft.getInstance().player).backSlot.slotIndex) {
            int buffer = 0;

            @Override
            public void tick(List<ResourceLocation> $$0) {
                  buffer++;

                  if (buffer < 30 && buffer > -1) {
                        super.tick($$0);
                  }
                  if (buffer == 30)
                        buffer = -40;
            }
      };

      public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
            super(screenHandler, playerInventory, text);
      }

      @Inject(method = "containerTick", at = @At("HEAD"))
      public void containerTick(CallbackInfo ci) {
            this.backSlotIcon.tick(Tooltip.getTextures());
      }

      @Override
      protected void renderTooltip(GuiGraphics gui, int mouseX, int mouseY) {
            if (this.hoveredSlot != null && Services.COMPAT.isBackSlot(this.hoveredSlot)) {
                  ItemStack itemstack = this.hoveredSlot.getItem();
                  BackData backData = BackData.get(minecraft.player);
                  if (!itemstack.isEmpty() && !backData.getBackpackInventory().isEmpty() && Kind.isBackpack(itemstack)) {
                        BackpackTooltip tooltip = new BackpackTooltip(backData.getBackpackInventory().getItemStacks());
                        gui.renderTooltip(minecraft.font, List.of(Component.empty()), Optional.of(tooltip), mouseX - 10000, mouseY - 10000);
                        return;
                  }
            }
            super.renderTooltip(gui, mouseX, mouseY);
      }

      @Inject(method = "renderBg", at = @At("TAIL"))
      protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
            BackSlot backSlot = BackData.get(this.minecraft.player).backSlot;
            if (backSlot.isActive() && backSlot.slotIndex != -1)
                  this.backSlotIcon.render(this.menu, context, delta, this.leftPos, this.topPos);
            if (!recipeBookComponent.isVisible() && infoWidget.isFocused())
                  infoWidget.render(context, mouseX, mouseY, delta);
      }

      @Unique private final InfoWidget infoWidget = new InfoWidget();
      @Inject(method = "init", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                  target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;setInitialFocus(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V"))
      public void backpackHelpWidget(CallbackInfo ci) {
            infoWidget.init(this.height, this.leftPos, this.topPos, this.minecraft, this.recipeBookComponent, () -> {
                  this.leftPos = this.infoWidget.updateScreenPosition(this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth), recipeBookComponent.isVisible(), this.width, this.imageWidth);
                  this.buttonClicked = true;
            });
            this.addWidget(this.infoWidget);
            this.addRenderableWidget(infoWidget.hideButton);
            for (Optional<InfoWidget.InfoButton> button : infoWidget.buttons) {
                  button.ifPresent(this::addRenderableWidget);
            }
      }

      @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
      private GuiEventListener captureRecipeBookButton(GuiEventListener button) {
            if (button instanceof ImageButton imageButton)
                  infoWidget.recipeButton = imageButton;
            return button;
      }

      @Inject(method = "mouseClicked", at = @At(value = "RETURN", ordinal = 1))
      public void hideHelpWidget(double $$0, double $$1, int $$2, CallbackInfoReturnable<Boolean> cir) {
            infoWidget.updateButtonPositions(leftPos);
            infoWidget.updateVisible();
      }
}
