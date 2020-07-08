package tfar.extratags.mixin;

import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.extratags.ExtraTags;
import tfar.extratags.api.ExtraTagManager;

@Mixin({ServerResourceManager.class})
public class ServerResourceManagerMixin {
	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	public ServerResourceManagerMixin() {
	}

	@Inject(
					method = {"<init>"},
					at = {@At("RETURN")}
	)
	private void addTags(CommandManager.RegistrationEnvironment registrationEnvironment, int i, CallbackInfo ci) {
		ExtraTags.instance.extraTagManager = new ExtraTagManager();
		this.resourceManager.registerListener(ExtraTags.instance.extraTagManager);
	}

	@Inject(
					method = {"loadRegistryTags"},
					at = {@At("RETURN")}
	)
	private void applyExtraTags(CallbackInfo ci) {
		ExtraTags.instance.extraTagManager.sync();
	}
}
