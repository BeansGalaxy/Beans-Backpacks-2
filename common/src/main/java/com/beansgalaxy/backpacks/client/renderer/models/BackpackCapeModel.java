package com.beansgalaxy.backpacks.client.renderer.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;


public class BackpackCapeModel<T extends Entity> extends EntityModel<T> {
	public final ModelPart cape;

	public BackpackCapeModel(ModelPart root) {
		this.cape = root.getChild("cape");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition cape = partdefinition.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 2.0F));

		PartDefinition right_cape_r1 = cape.addOrReplaceChild("right_cape_r1", CubeListBuilder.create().texOffs(1, -3).addBox(-4.0F, 0.0F, 1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, -3).addBox(4.0F, 0.0F, 1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition back_cape_r1 = cape.addOrReplaceChild("back_cape_r1", CubeListBuilder.create().texOffs(2, 2).addBox(-4.0F, 1.0F, 0.0F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(-1, 11).addBox(-4.0F, 1.0F, 0.0F, 8.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition front_cape_r1 = cape.addOrReplaceChild("front_cape_r1", CubeListBuilder.create().texOffs(2, 8).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(-1, 5).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		cape.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}