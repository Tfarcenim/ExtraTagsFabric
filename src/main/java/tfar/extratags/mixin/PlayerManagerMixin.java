package tfar.extratags.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.extratags.Hooks;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin implements ServerLoginPacketListener {
	@Inject(method = "onPlayerConnect", at = @At("RETURN"))
	public void onPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		Hooks.onPlayerLogin(connection, player, info);
	}
}
