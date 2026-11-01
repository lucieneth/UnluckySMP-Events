package unlucky.events.mixin;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unlucky.events.EventsCommand;

@Mixin(Commands.class)
public class CommandsMixin {

	// Register /unluckyevents after vanilla commands are set up. Runs on every dispatcher
	// rebuild (server start and /reload), so it stays registered.
	@Inject(method = "<init>", at = @At("RETURN"))
	private void unluckyevents$registerCommands(Commands.CommandSelection selection, CommandBuildContext context, CallbackInfo ci) {
		EventsCommand.register(((Commands) (Object) this).getDispatcher());
	}
}
