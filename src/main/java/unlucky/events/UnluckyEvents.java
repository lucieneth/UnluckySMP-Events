package unlucky.events;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnluckyEvents implements ModInitializer {
	public static final String MOD_ID = "unluckyevents";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Live config; replaced wholesale by /unluckyevents reload. Read from mixins and the server tick. */
	public static volatile EventsConfig CONFIG = new EventsConfig();

	@Override
	public void onInitialize() {
		CONFIG = EventsConfig.load();
		LOGGER.info("Unlucky Events loaded.");
	}
}
