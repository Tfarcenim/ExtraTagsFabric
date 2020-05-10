package tfar.extratags.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import tfar.extratags.api.tagtypes.BiomeTags;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;
import tfar.extratags.api.tagtypes.DimensionTypeTags;
import tfar.extratags.api.tagtypes.EnchantmentTags;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExtraTagManager implements ResourceReloadListener {

	private final RegistryTagContainer<Enchantment> enchantments = new RegistryTagContainer<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
	private final RegistryTagContainer<BlockEntityType<?>> block_entity_types = new RegistryTagContainer<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");
	private final RegistryTagContainer<Biome> biomes = new RegistryTagContainer<>(Registry.BIOME, "tags/biomes", "biome");
	private final RegistryTagContainer<DimensionType> dimension_types = new RegistryTagContainer<>(Registry.DIMENSION_TYPE, "tags/dimension_types", "dimension_types");

	public RegistryTagContainer<Enchantment> getEnchantments() {
		return this.enchantments;
	}

	public RegistryTagContainer<BlockEntityType<?>> getBlockEntityTypes() {
		return this.block_entity_types;
	}

	public RegistryTagContainer<Biome> getBiomes() {
		return this.biomes;
	}

	public RegistryTagContainer<DimensionType> getDimensionTypes() {
		return this.dimension_types;
	}

	public void write(PacketByteBuf buffer) {
		this.enchantments.toPacket(buffer);
		this.block_entity_types.toPacket(buffer);
		this.biomes.toPacket(buffer);
		this.dimension_types.toPacket(buffer);
	}

	public static ExtraTagManager read(PacketByteBuf buffer) {
		ExtraTagManager tagManager = new ExtraTagManager();
		tagManager.getEnchantments().fromPacket(buffer);
		tagManager.getBlockEntityTypes().fromPacket(buffer);
		tagManager.getBiomes().fromPacket(buffer);
		tagManager.getDimensionTypes().fromPacket(buffer);
		return tagManager;
	}

	@Override
	public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		CompletableFuture<Map<Identifier, Tag.Builder<Enchantment>>> enchantmentReload = this.enchantments.prepareReload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<BlockEntityType<?>>>> blockEntityReload = this.block_entity_types.prepareReload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<Biome>>> biomeReload = this.biomes.prepareReload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<DimensionType>>> dimensionTypeReload = this.dimension_types.prepareReload(resourceManager, backgroundExecutor);

		return enchantmentReload.thenCombine(blockEntityReload, Pair::of)
						.thenCombine(biomeReload.thenCombine(dimensionTypeReload,Pair::of),
										(first, second) ->
										new ReloadResults(first.getFirst(), first.getSecond(),second.getFirst(), second.getSecond()))
						.thenCompose(synchronizer::whenPrepared)
						.thenAcceptAsync(reloadResults -> {
							this.enchantments.applyReload(reloadResults.enchantments);
							this.block_entity_types.applyReload(reloadResults.blockEntites);
							this.biomes.applyReload(reloadResults.biomes);
							this.dimension_types.applyReload(reloadResults.dimension_types);

							EnchantmentTags.setContainer(this.enchantments);
							BlockEntityTypeTags.setContainer(this.block_entity_types);
							BiomeTags.setContainer(this.biomes);
							DimensionTypeTags.setContainer(this.dimension_types);
						}, gameExecutor);
	}

	public static class ReloadResults {
		final Map<Identifier, Tag.Builder<Enchantment>> enchantments;
		final Map<Identifier, Tag.Builder<BlockEntityType<?>>> blockEntites;
		final Map<Identifier, Tag.Builder<Biome>> biomes;
		final Map<Identifier, Tag.Builder<DimensionType>> dimension_types;

		public ReloadResults(Map<Identifier, Tag.Builder<Enchantment>> enchantments,
												 Map<Identifier, Tag.Builder<BlockEntityType<?>>> blockEntities,
												 Map<Identifier, Tag.Builder<Biome>> biomes,
												 Map<Identifier, Tag.Builder<DimensionType>> dimension_types) {
			this.enchantments = enchantments;
			this.blockEntites = blockEntities;
			this.biomes = biomes;
			this.dimension_types = dimension_types;
		}
	}
}