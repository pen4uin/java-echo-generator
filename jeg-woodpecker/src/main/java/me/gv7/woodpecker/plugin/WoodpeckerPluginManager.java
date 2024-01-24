package me.gv7.woodpecker.plugin;

import me.gv7.woodpecker.helper.jEGHelper;

public class WoodpeckerPluginManager implements IPluginManager {
    public WoodpeckerPluginManager() {
    }

    @Override
    public void registerPluginManagerCallbacks(IPluginManagerCallbacks pluginManagerCallbacks) {
        pluginManagerCallbacks.registerHelperPlugin(new jEGHelper());
    }
}
