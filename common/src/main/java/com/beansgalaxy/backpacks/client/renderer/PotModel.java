package com.beansgalaxy.backpacks.client.renderer;

import com.beansgalaxy.backpacks.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

import java.util.Objects;

public class PotModel<T extends Entity>
		extends EntityModel<T> {
	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart front;
	public final ModelPart back;
	public final ModelPart left;
	public final ModelPart right;

	public PotModel(ModelPart root) {
		this.body = root.getChild("bottom");
		this.head = root.getChild("head");
		this.front = root.getChild("north");
		this.back = root.getChild("south");
		this.left = root.getChild("east");
		this.right = root.getChild("west");
	}

	public NonNullList<ModelPart> getModelParts() {
		NonNullList<ModelPart> modelParts = NonNullList.create();

		modelParts.add(body);
		modelParts.add(head);
		modelParts.add(front);
		modelParts.add(back);
		modelParts.add(left);
		modelParts.add(right);

		return modelParts;
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition north = modelPartData.addOrReplaceChild("north", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, -5.0F));

		PartDefinition wall_r1 = north.addOrReplaceChild("wall_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 5.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition south = modelPartData.addOrReplaceChild("south", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 1.0F, 2.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, -5.0F));

		PartDefinition east = modelPartData.addOrReplaceChild("east", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, -5.0F));

		PartDefinition wall_r2 = east.addOrReplaceChild("wall_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 5.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition west = modelPartData.addOrReplaceChild("west", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, -5.0F));

		PartDefinition wall_r3 = west.addOrReplaceChild("wall_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 5.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition bottom = modelPartData.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(-3.0F, 1.0F, 2.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(16, 0).addBox(-2.0F, 1.0F, 3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, -5.0F));

		PartDefinition head = modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, 3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, -5.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

	public void renderBody(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, boolean renderHead) {
		body.render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);
		if (renderHead)
			head.render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);
	}

	public void renderDetail(PoseStack matrices, MultiBufferSource mbs, int light, int overlay, DecoratedPotBlockEntity.Decorations sherds) {
		VertexConsumer vFront = mbs.getBuffer(renderType(SherdTexture.toTexture(sherds.front())));
		front.render(matrices, vFront, light, overlay, 1, 1, 1, 1);
		VertexConsumer vLeft = mbs.getBuffer(renderType(SherdTexture.toTexture(sherds.left())));
		left.render(matrices, vLeft, light, overlay, 1, 1, 1, 1);
		VertexConsumer vRight = mbs.getBuffer(renderType(SherdTexture.toTexture(sherds.right())));
		right.render(matrices, vRight, light, overlay, 1, 1, 1, 1);
	}

	@Override
	public void setupAnim(T entity, float f, float g, float h, float i, float j) {

	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);
		head.render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);
	}

	public enum SherdTexture {
		ANGLER(	Items.ANGLER_POTTERY_SHERD),
		ARCHER(	Items.ARCHER_POTTERY_SHERD),
		ARMS_UP(	Items.ARMS_UP_POTTERY_SHERD),
		BLADE(	Items.BLADE_POTTERY_SHERD),
		BREWER(	Items.BREWER_POTTERY_SHERD),
		BURN(		Items.BURN_POTTERY_SHERD),
		DANGER(	Items.DANGER_POTTERY_SHERD),
		EXPLORER(	Items.EXPLORER_POTTERY_SHERD),
		FRIEND(	Items.FRIEND_POTTERY_SHERD),
		HEART(	Items.HEART_POTTERY_SHERD),
		HEARTBREAK(	Items.HEARTBREAK_POTTERY_SHERD),
		HOWL(		Items.HOWL_POTTERY_SHERD),
		MINER(	Items.MINER_POTTERY_SHERD),
		MOURNER(	Items.MOURNER_POTTERY_SHERD),
		PLENTY(	Items.PLENTY_POTTERY_SHERD),
		PRIZE(	Items.PRIZE_POTTERY_SHERD),
		SHEAF(	Items.SHEAF_POTTERY_SHERD),
		SHELTER(	Items.SHELTER_POTTERY_SHERD),
		SKULL(	Items.SKULL_POTTERY_SHERD),
		SNORT(	Items.SNORT_POTTERY_SHERD);

		public static final ResourceLocation NONE = new ResourceLocation(Constants.MOD_ID, "textures/entity/clay_none.png");


		private final Item sherdItem;

		SherdTexture(Item item) {
			sherdItem = item;
		}

		private Item getItem() {
			return this.sherdItem;
		}

		private ResourceLocation getTexture() {
			String name = this.name().toLowerCase();
			return new ResourceLocation(Constants.MOD_ID, "textures/entity/clay/" + name + ".png");
		}

		public static ResourceLocation toTexture(Item item) {
			for (SherdTexture sherd : SherdTexture.values())
				if (Objects.equals(item.asItem(), sherd.getItem()))
					return sherd.getTexture();
			return NONE;
		}
	}

}