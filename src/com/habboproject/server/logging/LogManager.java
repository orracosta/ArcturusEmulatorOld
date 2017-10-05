package com.habboproject.server.logging;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;


public class LogManager implements Initializable {
    private static LogManager logManagerInstance;

    private Logger log = Logger.getLogger(LogManager.class.getName());

    public static final boolean ENABLED = Comet.getServer().getConfig().get("comet.game.logging.enabled").equals("true");

    private LogStore store;

    public LogManager() {
    }

    @Override
    public void initialize() {
        this.store = new LogStore();
    }

    public static LogManager getInstance() {
        if (logManagerInstance == null) {
            logManagerInstance = new LogManager();
        }

        return logManagerInstance;
    }


    public LogStore getStore() {
        return store;
    }
}