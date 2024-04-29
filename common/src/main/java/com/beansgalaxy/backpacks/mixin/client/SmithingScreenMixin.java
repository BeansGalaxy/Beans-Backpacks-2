package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.Viewable;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
            Minecraft minecraft = this.minecraft;
            this.backpackPreview = new Backpack(minecraft.level) {
                  @Override
                  public UUID getPlacedBy() {
                        return minecraft.player.getUUID();
                  }

                  @Override
                  public Traits.LocalData getTraits() {
                        ItemStack stack = smithingMenu.getSlot(3).getItem();
                        return Traits.LocalData.fromStack(stack, minecraft.player);
                  }

                  final Viewable viewable = new Viewable();

                  @Override
                  public Viewable getViewable() {
                        return viewable;
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
                        && backpackPreview.getTraits().maxStacks() != 0)
            {
                  graphics.pose().pushPose();

                  int scale = 50;
                  graphics.pose().translate(x, y - 8, 50.0);
                  Quaternionf rotation = new Quaternionf().rotationXYZ(0.43633232F, 3.5f, (float) Math.PI);

                  graphics.pose().mulPoseMatrix(new Matrix4f().scaling((float) scale, (float) scale, (float) (-scale)));
                  Lighting.setupForEntityInInventory();
                  graphics.pose().mulPose(rotation);
                  EntityRenderDispatcher $$8 = Minecraft.getInstance().getEntityRenderDispatcher();

                  $$8.setRenderShadow(false);
                  RenderSystem.runAsFancy(() -> $$8.render(backpackPreview, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880));
                  graphics.flush();
                  $$8.setRenderShadow(true);
                  graphics.pose().popPose();
                  Lighting.setupFor3DItems();
                  return true;
            }
            return false;
      }
}
