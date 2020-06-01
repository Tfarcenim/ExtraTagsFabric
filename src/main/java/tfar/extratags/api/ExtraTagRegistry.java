package tfar.extratags.api;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class ExtraTagRegistry {

	public static final ModTag<Biome> BIOME = new ModTag<>();
	public static final ModTag<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new ModTag<>();
	public static final ModTag<DimensionType> DIMENSION_TYPE = new ModTag<>();
	public static final ModTag<Enchantment> ENCHANTMENT = new ModTag<>();

	public static Tag<Biome> biome(Identifier identifier) {
		return BIOME.create(identifier);
	}

	public static Tag<BlockEntityType<?>> blockEntityType(Identifier identifier) {
		return BLOCK_ENTITY_TYPE.create(identifier);
	}

	public static Tag<DimensionType> dimensionType(Identifier identifier) {
		return DIMENSION_TYPE.create(identifier);
	}

	public static Tag<Enchantment> enchantment(Identifier identifier) {
		return ENCHANTMENT.create(identifier);
	}

	public static <T> Tag<T> create(Identifier identifier, ModTag<T> tags) {
		return tags.create(identifier);
	}

	public static class Delayed {

		public static Tag<Biome> block(Identifier id) {
			return TagRegistry.create(id, BIOME::getContainer);
		}

		public static Tag<BlockEntityType<?>> entityType(Identifier id) {
			return TagRegistry.create(id, BLOCK_ENTITY_TYPE::getContainer);
		}

		public static Tag<DimensionType> fluid(Identifier id) {
			return TagRegistry.create(id, DIMENSION_TYPE::getContainer);
		}

		public static Tag<Enchantment> item(Identifier id) {
			return TagRegistry.create(id, ENCHANTMENT::getContainer);
		}
	}
}
