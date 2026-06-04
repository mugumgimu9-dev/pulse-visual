package com.pulsevisual.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import com.pulsevisual.client.manager.ModuleManager;
import com.pulsevisual.client.manager.ConfigManager;
import com.pulsevisual.client.manager.KeybindManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PulseVisualClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("PulseVisualClient");
    
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static KeybindManager keybindManager;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Pulse Visual Client");
        
        configManager = new ConfigManager();
        configManager.loadConfigs();
        
        moduleManager = new ModuleManager();
        moduleManager.initializeModules();
        
        keybindManager = new KeybindManager();
        keybindManager.registerKeybinds();
        
        LOGGER.info("Pulse Visual Client initialized successfully");
    }
}
