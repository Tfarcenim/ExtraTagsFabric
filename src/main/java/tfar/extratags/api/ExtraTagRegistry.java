package tfar.extratags.api;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class ExtraTagRegistry {
	public static final ModTag<Biome> BIOME = new ModTag<>();
	public static final ModTag<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new ModTag<>();
	public static final ModTag<Enchantment> ENCHANTMENT = new ModTag<>();
	public static final List<ModTag<?>> tagTypeList = new ArrayList<>();

	public ExtraTagRegistry() {
	}

	public static Tag.Identified<Biome> biome(String identifier) {
		return BIOME.register(identifier);
	}

	public static Tag.Identified<BlockEntityType<?>> blockEntityType(String identifier) {
		return BLOCK_ENTITY_TYPE.register(identifier);
	}

	public static Tag<Enchantment> enchantment(String identifier) {
		return ENCHANTMENT.register(identifier);
	}

	public static <T> Tag<T> create(String identifier, ModTag<T> tags) {
		return tags.register(identifier);
	}

	static {
		tagTypeList.add(BIOME);
		tagTypeList.add(BLOCK_ENTITY_TYPE);
		tagTypeList.add(ENCHANTMENT);
	}

	public static class Delayed {
		public Delayed() {
		}

		public static Tag<Biome> biome(Identifier id) {
			return TagRegistry.create(id, ExtraTagRegistry.BIOME::getContainer);
		}

		public static Tag<BlockEntityType<?>> blockEntityType(Identifier id) {
			return TagRegistry.create(id, ExtraTagRegistry.BLOCK_ENTITY_TYPE::getContainer);
		}

		public static Tag<Enchantment> enchantment(Identifier id) {
			return TagRegistry.create(id, ExtraTagRegistry.ENCHANTMENT::getContainer);
		}
	}
}
