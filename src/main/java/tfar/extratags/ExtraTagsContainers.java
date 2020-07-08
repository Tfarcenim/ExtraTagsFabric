package tfar.extratags;

import net.minecraft.tag.TagContainer;
import tfar.extratags.api.ExtraTagRegistry;
import tfar.extratags.api.ModTag;

import java.util.Collection;
import java.util.stream.Collectors;

public class ExtraTagsContainers {
	private static volatile ExtraTagsContainers extraTagContainers;
	private final Collection<? extends TagContainer> containers;

	private ExtraTagsContainers(Collection<? extends TagContainer> containers) {
		this.containers = containers;
	}

	public static ExtraTagsContainers instance() {
		return extraTagContainers;
	}

	public static void setHolder(Collection<? extends TagContainer> tagContainerHolders) {
		extraTagContainers = new ExtraTagsContainers(tagContainerHolders);
	}

	static {
		extraTagContainers = new ExtraTagsContainers(ExtraTagRegistry.tagTypeList.stream().map(ModTag::getContainer).collect(Collectors.toList()));
	}
}