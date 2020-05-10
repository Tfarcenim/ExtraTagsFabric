package tfar.extratags.api.tagtypes;

import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.Optional;

public class DimensionTypeTags {

	private static TagContainer<DimensionType> container = new TagContainer<>((identifier) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setContainer(RegistryTagContainer<DimensionType> container) {
		DimensionTypeTags.container = container;
		++generation;
	}

	public static TagContainer<DimensionType> getContainer() {
		return container;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<DimensionType> register(Identifier identifier) {
		return new CachingTag(identifier);
	}

	public static class CachingTag extends Tag<DimensionType> {
		private int lastKnownGeneration = -1;
		private Tag<DimensionType> cachedTag;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(DimensionType dimensionType) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(dimensionType);
		}

		@Override
		public Collection<DimensionType> values() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.values();
		}

		@Override
		public Collection<Entry<DimensionType>> entries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.entries();
		}
	}
}
