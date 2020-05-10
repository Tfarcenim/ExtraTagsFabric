package tfar.extratags.api.tagtypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;

public class EnchantmentTags {

	private static TagContainer<Enchantment> container = new TagContainer<>((identifier) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setContainer(RegistryTagContainer<Enchantment> container) {
		EnchantmentTags.container = container;
		++generation;
	}

	public static TagContainer<Enchantment> getContainer() {
		return container;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<Enchantment> register(Identifier identifier) {
		return new CachingTag(identifier);
	}

	public static class CachingTag extends Tag<Enchantment> {
		private int lastKnownGeneration = -1;
		private Tag<Enchantment> cachedTag;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(Enchantment enchantment) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(enchantment);
		}

		@Override
		public Collection<Enchantment> values() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.values();
		}

		@Override
		public Collection<Tag.Entry<Enchantment>> entries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = container.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.entries();
		}
	}
}
