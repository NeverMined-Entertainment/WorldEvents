package org.nevermined.worldevents.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.Configurable;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.configurables.MaterialConfigurableApi;

public class MaterialConfigurable implements Configurable, MaterialConfigurableApi {

    private Material material;

    public MaterialConfigurable(Material material) {
        this.material = material;
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        return material.toString();
    }

    @Override
    public void fromConfig(Object configObject) {
        this.material = Material.getMaterial((String)configObject);
    }

    public Material getMaterial() {
        return material;
    }
}
