package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.BackpackItem;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

      @ModifyVariable(method = "render", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                  target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"), argsOnly = true)
      private BakedModel injected(BakedModel value, ItemStack stack) {
            if (stack.getItem() instanceof BackpackItem && stack.getTag() != null) {
                  ModelManager modelManager = ((ItemRenderer) ((Object) this)).getItemModelShaper().getModelManager();
                  String key = stack.getOrCreateTagElement("display").getString("key");
                  if(!key.equals("leather") && !key.equals("winged"))
                        value = modelManager.getModel(beans_Backpacks_2$getBackpack(key));
            }
            return value;
      }

      @Unique
      private static ModelResourceLocation beans_Backpacks_2$getBackpack(String key) {
            return new ModelResourceLocation(Constants.MOD_ID, "backpack/" + key, "inventory");
      }
}
