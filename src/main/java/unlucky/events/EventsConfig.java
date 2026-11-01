package unlucky.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EventsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("unlucky-events.json");

	/**
	 * auto = follow vanilla's own dates, on = forced on all year,
	 * off = forced off (this also suppresses vanilla's own Oct 31 pumpkin heads).
	 */
	public enum Mode {
		@SerializedName("auto") AUTO,
		@SerializedName("on") ON,
		@SerializedName("off") OFF
	}

	// Time zone used for event dates, e.g. "Europe/Amsterdam". Blank = the server machine's own time zone.
	public String timeZone = "";
	// Broadcast a chat message when an event starts or ends.
	public boolean announceEventChanges = true;

	public Halloween halloween = new Halloween();

	/** Shared by every event. */
	public static class EventSettings {
		public Mode mode = Mode.AUTO;
	}

	public static class Halloween extends EventSettings {
		// Zombies and skeletons spawn wearing carved pumpkins / jack o'lanterns (vanilla: Oct 31 only).
		public boolean pumpkinHeads = true;
		// Old vanilla Halloween rule: bats spawn twice as often and in brighter caves (vanilla: Oct 20 to Nov 3).
		public boolean halloweenBats = true;
	}

	public static EventsConfig load() {
		try {
			if (Files.exists(PATH)) {
				try (var reader = Files.newBufferedReader(PATH)) {
					EventsConfig config = GSON.fromJson(reader, EventsConfig.class);
					if (config != null) {
						return config;
					}
				}
			}
		} catch (IOException | JsonParseException e) {
			UnluckyEvents.LOGGER.error("Failed to read {}, keeping defaults (file left untouched)", PATH, e);
			return new EventsConfig();
		}
		EventsConfig config = new EventsConfig();
		config.save();
		return config;
	}

	public void save() {
		try {
			Files.writeString(PATH, GSON.toJson(this));
		} catch (IOException e) {
			UnluckyEvents.LOGGER.error("Failed to write {}", PATH, e);
		}
	}
}
