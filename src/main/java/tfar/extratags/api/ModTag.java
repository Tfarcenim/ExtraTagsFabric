package tfar.extratags.api;

import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ModTag<T> {

	private final GlobalTagAccessor<T> ACCESSOR = new GlobalTagAccessor();

	public ModTag() {
	}

	public Tag.Identified<T> register(String id) {
		return this.ACCESSOR.get(id);
	}

	public void setContainer(TagContainer<T> container) {
		this.ACCESSOR.setContainer(container);
	}

	public void markReady() {
		this.ACCESSOR.markReady();
	}

	public TagContainer<T> getContainer() {
		return this.ACCESSOR.getContainer();
	}

	public Set<Identifier> set(TagContainer<T> tagContainer) {
		return this.ACCESSOR.method_29224(tagContainer);
	}
}
