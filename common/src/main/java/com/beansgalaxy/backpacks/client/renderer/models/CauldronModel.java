package com.beansgalaxy.backpacks.client.renderer.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;



public class CauldronModel<T extends Entity> extends EntityModel<T> {
	public final ModelPart fluid;
	public final ModelPart cauldron;

	public CauldronModel(ModelPart root) {
		this.cauldron = root.getChild("cauldron");
		this.fluid = root.getChild("fluid");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fluid = partdefinition.addOrReplaceChild("fluid", CubeListBuilder.create().texOffs(-8, 0).addBox(-4.0F, -7.0F, 1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, -5.0F));

		PartDefinition cauldron = partdefinition.addOrReplaceChild("cauldron", CubeListBuilder.create().texOffs(8, 0).addBox(-3.0F, -13.0F, 2.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(16, 22).addBox(-2.0F, -13.0F, 3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(8, 12).mirror().addBox(-3.0F, -7.0F, 2.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 31.0F, -5.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		cauldron.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}