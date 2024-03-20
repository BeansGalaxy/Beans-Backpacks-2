package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
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
            Item item = stack.getItem();
            if (item.equals(Services.REGISTRY.getMetal()) && stack.getTag() != null) {
                  ModelManager modelManager = ((ItemRenderer) ((Object) this)).getItemModelShaper().getModelManager();
                  CompoundTag tag = stack.getTag();
                  if (tag != null && tag.contains("backpack_id")) {
                        String backpack_id = tag.getString("backpack_id");
                        value = modelManager.getModel(new ModelResourceLocation(Constants.MOD_ID, "backpack/" + backpack_id, "inventory"));
                  }
            }
            return value;
      }
}
