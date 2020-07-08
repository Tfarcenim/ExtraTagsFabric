package tfar.extratags.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import tfar.extratags.ExtraTagsContainers;
import tfar.extratags.mixin.TagContainerAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ExtraTagManager<T> implements ResourceReloadListener {
	private final List<RegistryTagContainer<T>> tagCollections = new ArrayList<>();

	public ExtraTagManager() {
		RegistryTagContainer<Biome> biomes = new RegistryTagContainer<>(Registry.BIOME, "tags/biomes", "biome");
		this.tagCollections.add(biomes);
		RegistryTagContainer<BlockEntityType<?>> block_entity_types = new RegistryTagContainer<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");
		this.tagCollections.add(block_entity_types);
		RegistryTagContainer<Enchantment> enchantments = new RegistryTagContainer<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
		this.tagCollections.add(enchantments);
	}

	public void write(PacketByteBuf buffer) {
		this.tagCollections.forEach((registryTagContainer) -> {
			registryTagContainer.toPacket(buffer);
		});
	}

	public static ExtraTagManager<?> read(PacketByteBuf buffer) {
		ExtraTagManager<?> tagManager = new ExtraTagManager<>();
		tagManager.tagCollections.forEach((tagCollection) -> {
			tagCollection.fromPacket(buffer);
		});
		return tagManager;
	}

	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor prepareExecutor, Executor applyExecutor) {
		List<CompletableFuture<Map<Identifier, Tag.Builder>>> completableFutureList = new ArrayList();

		for (RegistryTagContainer<T> tagCollection : this.tagCollections) {
			RegistryTagContainer<?> registryTagContainer = tagCollection;
			completableFutureList.add(registryTagContainer.prepareReload(manager, prepareExecutor));
		}

		CompletableFuture<Void> completableFutureVoid = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));
		synchronizer.getClass();
		return completableFutureVoid.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
			for(int ix = 0; ix < this.tagCollections.size(); ++ix) {
				RegistryTagContainer registryTagContainer = this.tagCollections.get(ix);
				registryTagContainer.applyReload(completableFutureList.get(ix).join());
			}

			ExtraTagsContainers.setHolder(this.tagCollections);
			Multimap<String, Identifier> multimap = HashMultimap.create();

			for(int i = 0; i < this.tagCollections.size(); ++i) {
				RegistryTagContainer registryTagContainerx = this.tagCollections.get(i);
				String entryType = ((TagContainerAccessor)registryTagContainerx).getEntryType();
				multimap.putAll(entryType, ExtraTagRegistry.tagTypeList.get(i).set(registryTagContainerx));
			}

			if (!multimap.isEmpty()) {
				throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map((entry) -> {
					return entry.getKey() + ":" + entry.getValue();
				}).sorted().collect(Collectors.joining(",")));
			}
		}, applyExecutor);
	}

	public void sync() {
		for(int i = 0; i < this.tagCollections.size(); ++i) {
			ModTag<?> modTag = ExtraTagRegistry.tagTypeList.get(i);
			modTag.setContainer(this.tagCollections.get(i));
		}

	}
}