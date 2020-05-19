package tfar.extratags.api;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import tfar.extratags.api.tagtypes.BiomeTags;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;
import tfar.extratags.api.tagtypes.DimensionTypeTags;
import tfar.extratags.api.tagtypes.EnchantmentTags;

public class ExtraTagRegistry {

	public static Tag<Biome> biome(Identifier id) {
		return TagRegistry.create(id, BiomeTags::getContainer);
	}

	public static Tag<BlockEntityType<?>> blockEntityType(Identifier id) {
		return TagRegistry.create(id, BlockEntityTypeTags::getContainer);
	}

	public static Tag<DimensionType> dimensionType(Identifier id) {
		return TagRegistry.create(id, DimensionTypeTags::getContainer);
	}

	public static Tag<Enchantment> enchantment(Identifier id) {
		return TagRegistry.create(id, EnchantmentTags::getContainer);
	}
}
