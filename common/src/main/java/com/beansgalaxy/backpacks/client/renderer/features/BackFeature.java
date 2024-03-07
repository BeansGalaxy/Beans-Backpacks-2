package com.beansgalaxy.backpacks.client.renderer.features;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.beansgalaxy.backpacks.client.RendererHelper.sneakInter;

public class BackFeature<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private final BackpackFeature<T, M> backpackFeature;
    private final PotFeature<T, M> potFeature;
    private final ElytraFeature<T, M> elytraFeature;
    protected float sneakInter = 0;

    public BackFeature(RenderLayerParent<T, M> context, EntityModelSet loader, ModelManager modelManager) {
        super(context);
        backpackFeature = new BackpackFeature<>(loader, modelManager, this);
        potFeature = new PotFeature<>(loader, this);
        elytraFeature = new ElytraFeature<>(loader, this);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource mbs, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float yHeadRot, float headPitch) {
        if (entity instanceof AbstractClientPlayer player) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            ModelPart torso = ((PlayerModel<?>) this.getParentModel()).body;
            Kind kind = Kind.fromStack(backStack);

            sneakInter = sneakInter(player, sneakInter);

            if (Kind.isBackpack(backStack))
                backpackFeature.render(pose, mbs, light, player, torso, backData);
            else if (Kind.POT.is(kind))
                potFeature.render(pose, mbs, light, player, torso, backStack);

            if (Kind.isWings(backStack))
                elytraFeature.render(pose, mbs, light, entity, limbAngle, limbDistance, animationProgress, yHeadRot, headPitch, this.getParentModel(), backData);
        }
    }
}
