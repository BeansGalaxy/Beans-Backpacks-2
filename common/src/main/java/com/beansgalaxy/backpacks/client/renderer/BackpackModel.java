package com.beansgalaxy.backpacks.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BackpackModel<T extends Entity>
		extends EntityModel<T> {
	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart mask;

	public BackpackModel(ModelPart root) {
		this.body = root.getChild("body");
		this.head = root.getChild("body").getChild("head");
		this.mask = root.getChild("body").getChild("mask_r1");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition body = modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition mask_r1 = body.addOrReplaceChild("mask_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -6.0F, 2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 3.1416F, 0.0F));

		PartDefinition bottom_r1 = body.addOrReplaceChild("bottom_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, 7.0F, -6.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(26, 0).addBox(-1.0F, 1.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, -1.5708F, 3.1416F, 0.0F));

		PartDefinition tMask1_r1 = head.addOrReplaceChild("tMask1_r1", CubeListBuilder.create().texOffs(8, 20).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, -0.3229F, 3.1416F, 0.0F));

		PartDefinition tMask0_r1 = head.addOrReplaceChild("tMask0_r1", CubeListBuilder.create().texOffs(11, 23).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, 0.0F, 3.1416F, 0.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float f, float g, float h, float i, float j) {

	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}