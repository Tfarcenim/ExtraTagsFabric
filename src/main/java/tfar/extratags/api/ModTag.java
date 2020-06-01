package tfar.extratags.api;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class ModTag<T> {

	private TagContainer<T> container = new TagContainer<>(location -> Optional.empty(), "", false, "");
	private int latestVersion;

	public void setContainer(TagContainer<T> container) {
		this.container = container;
		latestVersion++;
	}

	public TagContainer<T> getContainer() {
		return container;
	}

	public int getLatestVersion() {
		return latestVersion;
	}

	public Tag<T> create(Identifier identifier) {
		return new ModTag.CachingTag<>(identifier, this);
	}

	public static class CachingTag<T> extends Tag<T> {

		private final ModTag<T> delegate;
		private int version = -1;
		private Tag<T> cachedTag;

		public CachingTag(Identifier identifier, ModTag<T> delegate) {
			super(identifier);
			this.delegate = delegate;
		}

		private void validateCache() {
			int generation = delegate.getLatestVersion();
			if (this.version != generation) {
				this.cachedTag = delegate.getContainer().getOrCreate(getId());
				this.version = generation;
			}
		}

		@Override
		public boolean contains(@Nonnull T chemical) {
			validateCache();
			return this.cachedTag.contains(chemical);
		}

		@Nonnull
		@Override
		public Collection<T> values() {
			validateCache();
			return this.cachedTag.values();
		}

		@Nonnull
		@Override
		public Collection<Entry<T>> entries() {
			validateCache();
			return this.cachedTag.entries();
		}
	}
}
