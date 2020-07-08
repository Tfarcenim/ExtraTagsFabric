package tfar.extratags.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.extratags.api.ExtraTagRegistry;
import tfar.extratags.api.ModTag;

@Mixin({ClientPlayNetworkHandler.class})
public class ClientNetPlayManagerMixin {
	public ClientNetPlayManagerMixin() {
	}

	@Inject(
					method = {"onGameJoin"},
					at = {@At(
									value = "INVOKE",
									target = "Lnet/minecraft/tag/BlockTags;markReady()V"
					)}
	)
	private void markReady(GameJoinS2CPacket packet, CallbackInfo ci) {
		ExtraTagRegistry.tagTypeList.forEach(ModTag::markReady);
	}
}