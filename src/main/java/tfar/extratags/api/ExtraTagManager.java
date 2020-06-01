package tfar.extratags.api;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static tfar.extratags.api.ExtraTagRegistry.*;

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

	private final Map<RegistryTagContainer, Consumer<TagContainer>> tagCollections = new LinkedHashMap<>();

	public ExtraTagManager() {
		RegistryTagContainer<Enchantment> enchantments = new RegistryTagContainer<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
		tagCollections.put(enchantments, ENCHANTMENT::setContainer);
		RegistryTagContainer<BlockEntityType<?>> block_entity_types = new RegistryTagContainer<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");
		tagCollections.put(block_entity_types, BLOCK_ENTITY_TYPE::setContainer);
		RegistryTagContainer<Biome> biomes = new RegistryTagContainer<>(Registry.BIOME, "tags/biomes", "biome");
		tagCollections.put(biomes, BIOME::setContainer);
		RegistryTagContainer<DimensionType> dimension_types = new RegistryTagContainer<>(Registry.DIMENSION_TYPE, "tags/dimension_types", "dimension_types");
		tagCollections.put(dimension_types, DIMENSION_TYPE::setContainer);
	}

	public void write(PacketByteBuf buffer) {
		tagCollections.forEach(((RegistryTagContainer, tagCollectionConsumer) -> RegistryTagContainer.toPacket(buffer)));
	}

	public static ExtraTagManager read(PacketByteBuf buffer) {
		ExtraTagManager tagManager = new ExtraTagManager();

		tagManager.tagCollections.forEach((tagCollection, tagCollectionConsumer) -> tagCollection.fromPacket(buffer));
		return tagManager;
	}

	public void setContainers() {
		tagCollections.forEach((RegistryTagContainer, consumer) -> consumer.accept(RegistryTagContainer));
	}

	@Override
	public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
																				 {
		CompletableFuture<List<TagInfo<?>>> reloadResults = CompletableFuture.completedFuture(new ArrayList<>());
		for (Map.Entry<RegistryTagContainer,Consumer<TagContainer>> tagCollection : tagCollections.entrySet()) {
			reloadResults = combine(reloadResults, tagCollection.getKey(), resourceManager, backgroundExecutor,tagCollection.getValue());
		}
		return reloadResults.thenCompose(synchronizer::whenPrepared).thenAcceptAsync(results -> {
			results.forEach(TagInfo::applyAndSet);

		}, gameExecutor);
	}

	private CompletableFuture<List<TagInfo<?>>> combine(CompletableFuture<List<TagInfo<?>>> reloadResults,
																											RegistryTagContainer<?> tagCollection, ResourceManager resourceManager, Executor backgroundExecutor,Consumer<TagContainer> consumer) {
		return reloadResults.thenCombine(tagCollection.prepareReload(resourceManager, backgroundExecutor), (results, result) -> {
			results.add(new TagInfo(tagCollection, result, consumer));
			return results;
		});
	}

	public static class TagInfo<T> {

		private final RegistryTagContainer<T> tagCollection;
		final Map<Identifier, Tag.Builder<T>> results;
		final Consumer<TagContainer<?>> consumer;

		private TagInfo(RegistryTagContainer<T> tagCollection, Map<Identifier, Tag.Builder<T>> result, Consumer<TagContainer<?>> consumer) {
			this.tagCollection = tagCollection;
			this.results = result;
			this.consumer = consumer;
		}

		private void applyAndSet() {
			tagCollection.applyReload(results);
			consumer.accept(tagCollection);
		}
	}
}