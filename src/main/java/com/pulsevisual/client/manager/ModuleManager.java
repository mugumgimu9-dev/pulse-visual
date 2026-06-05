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
        registerModule(new FullbrightModule());
        registerModule(new ESPModule());
        registerModule(new TracersModule());
        registerModule(new NameTagsModule());
        registerModule(new CustomCrosshairModule());
        registerModule(new HUDModule());
        
        // Combat modules
        registerModule(new DamageParticlesModule());
        registerModule(new KillEffectsModule());
        registerModule(new CriticalHitModule());
        registerModule(new TotemsModule());
        
        // Utility modules
        registerModule(new KeystrokesModule());
        registerModule(new CPSModule());
        registerModule(new ArmorStatusModule());
        registerModule(new PotionStatusModule());
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
