package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

public class Constants {

	public static final String MOD_ID = "beansbackpacks";
	public static final String MOD_NAME = "Beans' Backpacks";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final HashMap<String, Traits> TRAITS_MAP = new HashMap<>();
	public static final boolean SLOTS_MOD_ACTIVE = Services.COMPAT.anyModsLoaded(new String[]{CompatHelper.CURIOS, CompatHelper.TRINKETS});
	public static final HashSet<Item> CHESTPLATE_DISABLED = new HashSet<>();
	public static final HashSet<Item> DISABLES_BACK_SLOT = new HashSet<>();

	public static Item itemFromString(String string) {
		if (string == null)
			return Items.AIR;
		String[] location = string.split(":");
		ResourceLocation resourceLocation = new ResourceLocation(location[0], location[1]);
            return BuiltInRegistries.ITEM.get(resourceLocation);
	}

	public static void disableFromChestplate(String string) {
		Item item = itemFromString(string);
		CHESTPLATE_DISABLED.add(item.asItem());
	}

	public static ItemStack getTorsoWearables(Player player, Item item) {
		ItemStack backSlot = BackData.get(player).getStack();
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return backSlot.is(item) ? backSlot : chestplate;
	}

	public static void disablesBackSlot(String string) {
		Item item = itemFromString(string);
		DISABLES_BACK_SLOT.add(item.asItem());
	}

}