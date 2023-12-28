package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class Constants {

	public static final String MOD_ID = "beansbackpacks";
	public static final String MOD_NAME = "Beans' Backpacks";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static HashSet<String> BACKPACK_KEYS = new HashSet<>();
	public static HashSet<Item> CHESTPLATE_DISABLED = new HashSet<>();
	public static HashSet<Item> DISABLES_BACK_SLOT = new HashSet<>();

	public static void disableFromChestplate(String string) {
		String[] location = string.split(":");
		ResourceLocation resourceLocation = new ResourceLocation(location[0], location[1]);
		Item item = BuiltInRegistries.ITEM.get(resourceLocation);
		CHESTPLATE_DISABLED.add(item.asItem());
	}

	public static ItemStack getTorsoWearables(Player player, Item item) {
		ItemStack backSlot = BackSlot.get(player).getItem();
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return backSlot.is(item) ? backSlot : chestplate;
	}

	public static void disablesBackSlot(String string) {
		String[] location = string.split(":");
		ResourceLocation resourceLocation = new ResourceLocation(location[0], location[1]);
		Item item = BuiltInRegistries.ITEM.get(resourceLocation);
		DISABLES_BACK_SLOT.add(item.asItem());
	}

}