package unlucky.events.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import unlucky.events.EventManager;

/**
 * Restores the Halloween bat rule older versions had (Oct 20 to Nov 3): bats skipped the 50%
 * spawn rejection and accepted light up to 7 instead of 4. Current versions dropped the date
 * check, so this brings it back for the Halloween event.
 */
@Mixin(Bat.class)
public class BatMixin {

	@Redirect(method = "checkBatSpawnRules",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z"))
	private static boolean unluckyevents$skipCoinFlip(RandomSource random) {
		return !EventManager.halloweenBats() && random.nextBoolean();
	}

	@ModifyConstant(method = "checkBatSpawnRules", constant = @Constant(intValue = 4))
	private static int unluckyevents$maxLight(int maxLight) {
		return EventManager.halloweenBats() ? 7 : maxLight;
	}
}
