package tfar.extratags.api.tagtypes;

import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.Collection;
import java.util.Optional;

public class BiomeTags {

	private static TagContainer<Biome> container = new TagContainer<>((identifier) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setContainer(RegistryTagContainer<Biome> container) {
		BiomeTags.container = container;
		++generation;
	}

	public static TagContainer<Biome> getContainer() {
		return container;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<Biome> register(Identifier identifier) {
		return new CachingTag(identifier);
	}

	public static class CachingTag extends Tag<Biome> {
		private int lastKnownGeneration = -1;
		private Tag<Biome> cachedTag;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(Biome biome) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(biome);
		}

		@Override
		public Collection<Biome> values() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.values();
		}

		@Override
		public Collection<Entry<Biome>> entries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.entries();
		}
	}
}
