package unlucky.events;

import net.minecraft.ChatFormatting;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.function.Function;

/** The special dates vanilla Minecraft itself knows about that the server can act on. */
public enum SeasonalEvent {
	// Oct 20 to Nov 3 is the window older versions used for Halloween bats; vanilla's
	// pumpkin heads only appear on Oct 31 inside it (see EventManager.pumpkinHeads).
	HALLOWEEN("halloween", "Halloween", ChatFormatting.GOLD, c -> c.halloween,
			MonthDay.of(10, 20), MonthDay.of(11, 3),
			"Halloween season has begun! Bats are swarming the caves.",
			"Halloween is over. The bats settle down.");

	public final String id;
	public final String displayName;
	public final ChatFormatting color;
	private final Function<EventsConfig, EventsConfig.EventSettings> settings;
	public final MonthDay start;
	public final MonthDay end;
	public final String startMessage;
	public final String endMessage;

	SeasonalEvent(String id, String displayName, ChatFormatting color,
	              Function<EventsConfig, EventsConfig.EventSettings> settings, MonthDay start, MonthDay end,
	              String startMessage, String endMessage) {
		this.id = id;
		this.displayName = displayName;
		this.color = color;
		this.settings = settings;
		this.start = start;
		this.end = end;
		this.startMessage = startMessage;
		this.endMessage = endMessage;
	}

	public EventsConfig.EventSettings settings() {
		return settings.apply(UnluckyEvents.CONFIG);
	}

	public boolean isActive(MonthDay today) {
		return switch (settings().mode) {
			case ON -> true;
			case OFF -> false;
			case AUTO -> !today.isBefore(start) && !today.isAfter(end);
		};
	}

	public static ZoneId zone() {
		String configured = UnluckyEvents.CONFIG.timeZone;
		if (configured != null && !configured.isBlank()) {
			try {
				return ZoneId.of(configured.trim());
			} catch (DateTimeException e) {
				// Fall through to the system zone; /unluckyevents shows which zone is actually in use.
			}
		}
		return ZoneId.systemDefault();
	}

	public static MonthDay today() {
		return MonthDay.now(zone());
	}
}
