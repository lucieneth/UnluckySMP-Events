package unlucky.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.time.MonthDay;
import java.util.EnumMap;
import java.util.Map;

/**
 * Re-evaluates the date once a second from the server tick and caches the result, so the
 * spawn-rule mixins (called many times per tick) never touch the clock themselves.
 */
public final class EventManager {
	private static final MonthDay HALLOWEEN_DAY = MonthDay.of(10, 31);
	private static final Map<SeasonalEvent, Boolean> ACTIVE = new EnumMap<>(SeasonalEvent.class);
	private static volatile MonthDay today = SeasonalEvent.today();
	private static boolean initialized;

	private EventManager() {
	}

	public static boolean isActive(SeasonalEvent event) {
		Boolean active = ACTIVE.get(event);
		return active != null ? active : event.isActive(today);
	}

	/** Vanilla only puts pumpkins on mobs on Oct 31 itself, so auto mode keeps that; "on" means every day. */
	public static boolean pumpkinHeads() {
		EventsConfig.Halloween config = UnluckyEvents.CONFIG.halloween;
		if (!config.pumpkinHeads) {
			return false;
		}
		return switch (config.mode) {
			case ON -> true;
			case OFF -> false;
			case AUTO -> today.equals(HALLOWEEN_DAY);
		};
	}

	public static boolean halloweenBats() {
		return UnluckyEvents.CONFIG.halloween.halloweenBats && isActive(SeasonalEvent.HALLOWEEN);
	}

	/** Re-evaluates immediately (after a command or reload) instead of waiting for the next second. */
	public static void refresh(MinecraftServer server) {
		today = SeasonalEvent.today();
		for (SeasonalEvent event : SeasonalEvent.values()) {
			boolean now = event.isActive(today);
			Boolean before = ACTIVE.put(event, now);
			// The first evaluation after startup only records state, so restarts don't re-announce.
			if (initialized && before != null && before != now && UnluckyEvents.CONFIG.announceEventChanges) {
				announce(server, event, now);
			}
		}
		initialized = true;
	}

	public static void tick(MinecraftServer server) {
		if (server.getTickCount() % 20 == 0) {
			refresh(server);
		}
	}

	private static void announce(MinecraftServer server, SeasonalEvent event, boolean started) {
		Component message = Component.literal("[" + event.displayName + "] ").withStyle(event.color, ChatFormatting.BOLD)
				.append(Component.literal(started ? event.startMessage : event.endMessage).withStyle(ChatFormatting.WHITE));
		server.getPlayerList().broadcastSystemMessage(message, false);
	}
}
