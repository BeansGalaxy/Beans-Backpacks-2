package com.beansgalaxy.backpacks.client.renderer.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BackpackModel<T extends Entity>
		extends EntityModel<T> {
	public final ModelPart main;
	public final ModelPart body;
	public final ModelPart mask;

	public BackpackModel(ModelPart root) {
		this.main = root.getChild("main");
		this.body = main.getChild("body");
		this.mask = main.getChild("mask");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition mask = main.addOrReplaceChild("mask", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bottom_r1 = mask.addOrReplaceChild("bottom_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -6.0F, 2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 3.1416F, 0.0F));

		PartDefinition mask_head = mask.addOrReplaceChild("mask_head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition spine_r1 = mask_head.addOrReplaceChild("spine_r1", CubeListBuilder.create().texOffs(8, 20).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, -0.3229F, 3.1416F, 0.0F));

		PartDefinition top_r1 = mask_head.addOrReplaceChild("top_r1", CubeListBuilder.create().texOffs(11, 23).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bottom_r2 = body.addOrReplaceChild("bottom_r2", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, 7.0F, -6.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition body_head = body.addOrReplaceChild("body_head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(26, 0).addBox(-1.0F, 1.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float f, float g, float h, float i, float j) {

	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public void renderMask(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		mask.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public void setupPlaced(float headPitch) {
		ModelPart[] modelParts = {body, mask};
		for (ModelPart part : modelParts) {
			part.zRot = (float) (180 * (Math.PI / 180));
			part.z = -4;
			part.y = 9;
		}

		setOpenAngle(headPitch);
	}

	public void setOpenAngle(float headPitch) {
		ModelPart[] topParts = {body.getChild("body_head"), mask.getChild("mask_head")};
		for (ModelPart topPart : topParts) {
			topPart.xRot = headPitch;
		}
	}
}