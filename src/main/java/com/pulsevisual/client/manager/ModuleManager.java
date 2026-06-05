package com.pulsevisual.client.manager;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import java.util.ArrayList;
import java.util.List;
import com.pulsevisual.client.module.Module;

@Environment(EnvType.CLIENT)
public class ModuleManager {
    private List<Module> modules = new ArrayList<>();

    public void initializeModules() {
        // Render modules
        registerModule(new com.pulsevisual.client.module.FullbrightModule());
        registerModule(new com.pulsevisual.client.module.ESPModule());
        registerModule(new com.pulsevisual.client.module.TracersModule());
        registerModule(new com.pulsevisual.client.module.NameTagsModule());
        registerModule(new com.pulsevisual.client.module.CustomCrosshairModule());
        registerModule(new com.pulsevisual.client.module.HUDModule());
        
        // Combat modules
        registerModule(new com.pulsevisual.client.module.DamageParticlesModule());
        registerModule(new com.pulsevisual.client.module.KillEffectsModule());
        registerModule(new com.pulsevisual.client.module.CriticalHitModule());
        registerModule(new com.pulsevisual.client.module.TotemsModule());
        
        // Utility modules
        registerModule(new com.pulsevisual.client.module.KeystrokesModule());
        registerModule(new com.pulsevisual.client.module.CPSModule());
        registerModule(new com.pulsevisual.client.module.ArmorStatusModule());
        registerModule(new com.pulsevisual.client.module.PotionStatusModule());
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModuleByName(String name) {
        return modules.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public void updateModules() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onUpdate();
            }
        }
    }

    public void renderModules() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender();
            }
        }
    }
}
