package unlucky.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.Locale;

public final class EventsCommand {
	private EventsCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("unluckyevents")
				.executes(ctx -> {
					ctx.getSource().sendSuccess(EventsCommand::info, false);
					return 1;
				})
				.then(Commands.literal("reload")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.executes(ctx -> {
							UnluckyEvents.CONFIG = EventsConfig.load();
							EventManager.refresh(ctx.getSource().getServer());
							ctx.getSource().sendSuccess(
									() -> Component.literal("Unlucky Events config reloaded.").withStyle(ChatFormatting.GREEN), true);
							return 1;
						}))
				.then(modeCommand("enable", EventsConfig.Mode.ON))
				.then(modeCommand("disable", EventsConfig.Mode.OFF))
				.then(modeCommand("auto", EventsConfig.Mode.AUTO)));
	}

	/** /unluckyevents <enable|disable|auto> <event>, with one literal per event for tab completion. */
	private static LiteralArgumentBuilder<CommandSourceStack> modeCommand(String name, EventsConfig.Mode mode) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(name)
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
		for (SeasonalEvent event : SeasonalEvent.values()) {
			builder.then(Commands.literal(event.id).executes(ctx -> setMode(ctx.getSource(), event, mode)));
		}
		return builder;
	}

	private static int setMode(CommandSourceStack source, SeasonalEvent event, EventsConfig.Mode mode) {
		EventsConfig config = UnluckyEvents.CONFIG;
		event.settings().mode = mode;
		config.save();
		EventManager.refresh(source.getServer());
		boolean active = EventManager.isActive(event);
		source.sendSuccess(() -> Component.literal(event.displayName + " set to ").withStyle(ChatFormatting.GRAY)
				.append(modeLabel(mode))
				.append(Component.literal(" — currently ").withStyle(ChatFormatting.GRAY))
				.append(activeLabel(active)), true);
		return 1;
	}

	private static Component info() {
		String version = FabricLoader.getInstance().getModContainer(UnluckyEvents.MOD_ID)
				.map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("?");
		MutableComponent text = Component.literal("Unlucky Events v" + version).withStyle(ChatFormatting.GOLD)
				.append(Component.literal("\nMinecraft's own seasonal events, under operator control.").withStyle(ChatFormatting.WHITE));
		for (SeasonalEvent event : SeasonalEvent.values()) {
			EventsConfig.EventSettings s = event.settings();
			text.append(Component.literal("\n" + event.displayName + ": ").withStyle(event.color))
					.append(activeLabel(EventManager.isActive(event)))
					.append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
					.append(modeLabel(s.mode))
					.append(Component.literal(", auto dates " + day(event.start) + " to " + day(event.end) + ")")
							.withStyle(ChatFormatting.GRAY));
		}
		return text
				.append(Component.literal("\nTime zone: ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(SeasonalEvent.zone().getId()).withStyle(ChatFormatting.WHITE))
				.append(Component.literal("\nMade by ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("Lucien").withStyle(ChatFormatting.AQUA))
				.append(Component.literal(" & ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("Claude").withStyle(ChatFormatting.LIGHT_PURPLE))
				.append(Component.literal("\nConfig: ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("config/unlucky-events.json").withStyle(ChatFormatting.WHITE))
				.append(Component.literal(" — ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("/unluckyevents reload").withStyle(ChatFormatting.YELLOW));
	}

	private static String day(MonthDay date) {
		return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.getDayOfMonth();
	}

	private static MutableComponent activeLabel(boolean active) {
		return Component.literal(active ? "ACTIVE" : "inactive").withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED);
	}

	private static MutableComponent modeLabel(EventsConfig.Mode mode) {
		return switch (mode) {
			case ON -> Component.literal("forced on").withStyle(ChatFormatting.GREEN);
			case OFF -> Component.literal("forced off").withStyle(ChatFormatting.RED);
			case AUTO -> Component.literal("auto").withStyle(ChatFormatting.YELLOW);
		};
	}
}
