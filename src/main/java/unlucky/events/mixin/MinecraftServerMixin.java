package unlucky.events.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unlucky.events.EventManager;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "tickServer", at = @At("TAIL"))
	private void unluckyevents$tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		EventManager.tick((MinecraftServer) (Object) this);
	}
}
