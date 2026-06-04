package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PulseVisualMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("PulseVisual");
    public static final String MOD_ID = "pulse-visual";

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Pulse Visual Mod");
    }
}
