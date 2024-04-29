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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BackFeature<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private final BackpackFeature<T, M> backpackFeature;
    private final PotFeature<T, M> potFeature;
    private final ElytraFeature<T, M> elytraFeature;
    private final CauldronFeature<T, M> cauldronFeature;
    protected float sneakInter = 0;

    public BackFeature(RenderLayerParent<T, M> context, EntityModelSet loader, ModelManager modelManager) {
        super(context);
        backpackFeature = new BackpackFeature<>(loader, modelManager, this);
        potFeature = new PotFeature<>(loader, this);
        cauldronFeature = new CauldronFeature<>(loader, modelManager, this);
        elytraFeature = new ElytraFeature<>(loader, this);
    }

    public static void weld(ModelPart welded, ModelPart weldTo) {
          welded.xRot = weldTo.xRot;
          welded.yRot = weldTo.yRot;
          welded.zRot = weldTo.zRot;
          welded.x = weldTo.x;
          welded.y = weldTo.y;
          welded.z = weldTo.z;
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
                backpackFeature.render(pose, mbs, light, player, torso, backData, tickDelta);
            else if (Kind.POT.is(kind))
                potFeature.render(pose, mbs, light, player, torso, backStack);
            else if (Kind.CAULDRON.is(kind))
                cauldronFeature.render(pose, mbs, player, light, torso, backStack);

            if (Kind.isWings(backStack))
                elytraFeature.render(pose, mbs, light, entity, limbAngle, limbDistance, animationProgress, yHeadRot, headPitch, this.getParentModel(), backData);
        }
    }

    static float sneakInter(Entity entity, float sneakInter) {
        if (entity.isCrouching())
            sneakInter += sneakInter < 3 ? 1 : 0;
        else {
            sneakInter -= sneakInter > 1 ? 1 : 0;
            sneakInter -= sneakInter > 0 ? 1 : 0;
        }
        return sneakInter;
    }
}
