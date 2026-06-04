package com.pulsevisual.client.manager;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class ConfigManager {
    private File configDir;
    private Gson gson;

    public ConfigManager() {
        this.configDir = new File("pulse_visual");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void loadConfigs() {
        // Load all module configs
    }

    public void saveConfigs() {
        // Save all module configs
    }

    public void loadModuleConfig(String moduleName) {
        File file = new File(configDir, moduleName + ".json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                // Load config from JSON
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveModuleConfig(String moduleName, Object config) {
        File file = new File(configDir, moduleName + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
