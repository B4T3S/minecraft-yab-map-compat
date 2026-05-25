package es.b4t.client.mixin;

import es.b4t.client.MapDevourer;
import me.jfenn.bingo.client.impl.ClientNetworkingImpl;
import me.jfenn.bingo.client.platform.ClientPacket;
import me.jfenn.bingo.common.timer.CountdownPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkingImpl.ClientPacketHandlerS2C.class)
public class BingoS2CPacketHandlerMixin {
	@Inject(at = @At("TAIL"), method = "lambda$0$0")
	private static void handleGamestateChangePackets(ClientNetworkingImpl impl, ClientNetworkingImpl.ClientPacketHandlerS2C handler, ClientPacket packet, CallbackInfo ci) {
		// I know this is slightly janky, but I currently just don't know of a better way to implement this...
		// If we receive a countdown packet with the time set to 3 seconds, we reset all maps.
		if (packet.getPacket() instanceof CountdownPacket && ((CountdownPacket) packet.getPacket()).getSecondsRemaining() == 3) {
			MapDevourer.EradicateCurrentMap();
		}
	}
}