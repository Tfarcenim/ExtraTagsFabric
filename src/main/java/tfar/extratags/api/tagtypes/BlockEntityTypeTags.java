package tfar.extratags.api.tagtypes;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;

public class BlockEntityTypeTags {

	private static TagContainer<BlockEntityType<?>> container = new TagContainer<>((identifier) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setContainer(RegistryTagContainer<BlockEntityType<?>> container) {
		BlockEntityTypeTags.container = container;
		++generation;
	}

	public static TagContainer<BlockEntityType<?>> getContainer() {
		return container;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<BlockEntityType<?>> register(Identifier identifier) {
		return new CachingTag(identifier);
	}

	public static class CachingTag extends Tag<BlockEntityType<?>> {
		private int lastKnownGeneration = -1;
		private Tag<BlockEntityType<?>> cachedTag;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(BlockEntityType<?> blockEntityType) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(blockEntityType);
		}

		@Override
		public Collection<BlockEntityType<?>> values() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.values();
		}

		@Override
		public Collection<Tag.Entry<BlockEntityType<?>>> entries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.entries();
		}
	}
}
