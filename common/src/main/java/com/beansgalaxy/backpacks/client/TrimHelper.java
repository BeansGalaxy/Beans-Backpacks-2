package com.beansgalaxy.backpacks.client;

import com.beansgalaxy.backpacks.Constants;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TrimHelper extends ArmorTrim {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<TrimHelper> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group((TrimMaterial.CODEC.fieldOf("material")).forGetter(TrimHelper::material),
    (TrimPattern.CODEC.fieldOf("pattern")).forGetter(TrimHelper::pattern)).apply(instance, TrimHelper::new));

    private final Holder<TrimMaterial> material;
    private final Function<ArmorMaterial, ResourceLocation> backpackTexture;

    public TrimHelper(Holder<TrimMaterial> material, Holder<TrimPattern> pattern) {
        super(material, pattern);
        this.material = material;
        this.backpackTexture = Util.memoize(armorMaterial -> {
            ResourceLocation location = pattern.value().assetId();
            String materialString = this.getMaterialAssetNameFor(armorMaterial);
            ResourceLocation resourceLocation = location.withPath(in -> "trims/backpacks/" + in + "_" + materialString);
            return resourceLocation;
        });
    }

    private String getMaterialAssetNameFor(ArmorMaterial $$0) {
        Map<ArmorMaterials, String> map = this.material.value().overrideArmorMaterials();
        return $$0 instanceof ArmorMaterials && map.containsKey($$0) ? map.get($$0) : this.material.value().assetName();
    }

    public static Optional<TrimHelper> getBackpackTrim(RegistryAccess registryManager, CompoundTag tag) {
        if (!tag.isEmpty()) {
            TrimHelper b$trim = CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryManager), tag).resultOrPartial(LOGGER::error).orElse(null);
            return Optional.ofNullable(b$trim);
        } else return Optional.empty();
    }

    public ResourceLocation backpackTexture(ArmorMaterial p_268143_) {
        return this.backpackTexture.apply(p_268143_);
    }
}
