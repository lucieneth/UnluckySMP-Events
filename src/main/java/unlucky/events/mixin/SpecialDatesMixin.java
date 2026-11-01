package unlucky.events.mixin;

import net.minecraft.util.SpecialDates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unlucky.events.EventManager;

/**
 * Vanilla only puts pumpkins on zombies and skeletons when the real date is Oct 31; the check
 * lives in SpecialDates.isHalloween(), called from their finalizeSpawn. Answering it from our
 * Halloween settings means auto keeps Oct 31, "on" gives pumpkin heads any day and "off"
 * removes them even on Oct 31.
 */
@Mixin(SpecialDates.class)
public class SpecialDatesMixin {

	@Inject(method = "isHalloween", at = @At("HEAD"), cancellable = true)
	private static void unluckyevents$halloween(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(EventManager.pumpkinHeads());
	}
}
