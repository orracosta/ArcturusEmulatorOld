package com.habboproject.server.network.websocket.messages;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.websocket.messages.executors.PreviewPhotoCommandExecutor;
import com.habboproject.server.network.websocket.messages.executors.RequestFriendshipCommandExecutor;
import com.habboproject.server.utilities.JsonFactory;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by brend on 28/02/2017.
 */
public class WebSocketCommandHandler {
    public static Logger log = Logger.getLogger(WebSocketCommandHandler.class.getName());

    private final Map<String, Class<? extends WebSocketCommandExecutor>> commands = new ConcurrentHashMap<>();

    public WebSocketCommandHandler() {
        this.load();
    }

    public void load() {
        this.getCommands().put("preview_photo", PreviewPhotoCommandExecutor.class);
        this.getCommands().put("request_friend", RequestFriendshipCommandExecutor.class);
    }

    public void handle(String command, String data) {
        if (this.getCommands().containsKey(command)) {
            try {
                long start = System.currentTimeMillis();

                Class<? extends WebSocketCommandExecutor> classExecutor = this.getCommands().get(command);

                WebSocketCommandExecutor executor = classExecutor.getDeclaredConstructor().newInstance();

                log.debug("Started executor process for web command: [" + executor.getClass().getSimpleName() + "][" + command + "]");

                executor.handle(JsonFactory.getInstance().fromJson(data, executor.getType()));

                log.debug("Finished executor process for web command: [" + executor.getClass().getSimpleName() + "][" + command + "] in " + ((System.currentTimeMillis() - start)) + "ms");
            } catch (Exception e) {
                log.error("Error while handling command: " + command, e);
            }
        } else if (Comet.isDebugging) {
            log.debug("Unhandled WebSocket command: " + command);
        }
    }

    public Map<String, Class<? extends WebSocketCommandExecutor>> getCommands() {
        return commands;
    }
}
