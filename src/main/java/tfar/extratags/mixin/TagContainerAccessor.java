package tfar.extratags.mixin;

import net.minecraft.tag.TagContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({TagContainer.class})
public interface TagContainerAccessor {
	@Accessor
	String getEntryType();
}
