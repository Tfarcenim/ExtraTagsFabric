package tfar.extratags;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Hooks {


	public static void onPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		ExtraTags.instance.extraTagManager.write(buf);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ExtraTags.packet_id, buf);
	}
}
