package com.habboproject.server.modules;

import com.habboproject.server.api.config.ModuleConfig;
import com.habboproject.server.api.events.EventHandler;
import com.habboproject.server.api.modules.BaseModule;
import com.habboproject.server.api.server.IGameService;
import com.habboproject.server.modules.events.EventHandlerService;
import com.habboproject.server.utilities.Initializable;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleManager implements Initializable {
    private static ModuleManager moduleManagerInstance;
    private static final Logger log = Logger.getLogger(ModuleManager.class.getName());

    private EventHandler eventHandler;
    private CometGameService gameService;

    private Map<String, BaseModule> modules;

    public ModuleManager() {
        this.eventHandler = new EventHandlerService();
        this.gameService = new CometGameService(this.eventHandler);
    }

    public static ModuleManager getInstance() {
        if (moduleManagerInstance == null) {
            moduleManagerInstance = new ModuleManager();
        }

        return moduleManagerInstance;
    }

    @Override
    public void initialize() {
        if (this.modules != null) {
            this.modules.clear();
        } else {
            this.modules = new ConcurrentHashMap<>();
        }

        ModuleManager.getInstance().getEventHandler().initialize();

        for (String moduleName : this.findModules()) {
            try {
                this.loadModule(moduleName);
            } catch (Exception e) {
                log.warn("Error while loading module: " + moduleName, e);
            }
        }
    }

    private List<String> findModules() {
        List<String> results = new ArrayList<>();

        File[] files = new File("./modules").listFiles();

        if (files == null) return results;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                results.add(file.getName());
            }
        }

        return results;
    }

    private void loadModule(String name) throws Exception {
        URLClassLoader loader = URLClassLoader.newInstance(
                new URL[]{new URL("jar:file:modules/" + name + "!/")},
                getClass().getClassLoader()
        );

        URL configJsonLocation = loader.getResource("plugin.json");

        if (configJsonLocation == null) throw new Exception("plugin.json does not exist");

        final ModuleConfig moduleConfig = JsonFactory.getInstance().fromJson(Resources.toString(configJsonLocation, Charsets.UTF_8), ModuleConfig.class);

        if (this.modules.containsKey(moduleConfig.getName())) {
            if (!this.modules.get(moduleConfig.getName()).getConfig().getVersion().equals(moduleConfig.getVersion())) {
                log.warn("Modules with same name but different version was detected: " + moduleConfig.getName());
            }

            return;
        }

        log.info("Loaded module: " + moduleConfig.getName());

        Class<?> clazz = Class.forName(moduleConfig.getEntryPoint(), true, loader);
        Class<? extends BaseModule> runClass = clazz.asSubclass(BaseModule.class);
        Constructor<? extends BaseModule> ctor = runClass.getConstructor(ModuleConfig.class, IGameService.class);

        BaseModule cometModule = ctor.newInstance(moduleConfig, this.gameService);

        cometModule.loadModule();

        this.modules.put(moduleConfig.getName(), cometModule);

        loader.close();
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
