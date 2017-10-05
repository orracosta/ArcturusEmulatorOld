package com.habboproject.server.modules.events;

import com.habboproject.server.api.commands.CommandInfo;
import com.habboproject.server.api.events.Event;
import com.habboproject.server.api.events.EventArgs;
import com.habboproject.server.api.events.EventHandler;
import com.habboproject.server.api.networking.sessions.BaseSession;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class EventHandlerService implements EventHandler {
    private final ExecutorService asyncEventExecutor;
    private final Logger log = Logger.getLogger(EventHandlerService.class);

    private final Map<Class<?>, List<Event>> listeners;

    private final Map<String, BiConsumer<BaseSession, String[]>> chatCommands;
    private final Map<String, CommandInfo> commandInfo;

    public EventHandlerService() {
        this.asyncEventExecutor = Executors.newCachedThreadPool();

        this.listeners = Maps.newConcurrentMap();
        this.chatCommands = Maps.newConcurrentMap();
        this.commandInfo = Maps.newConcurrentMap();
    }

    public void initialize() {
        if(this.listeners != null) {
            this.listeners.clear();
        }

        if(this.chatCommands != null) {
            this.chatCommands.clear();
        }

        if(this.commandInfo != null) {
            this.commandInfo.clear();
        }
    }

    @Override
    public void registerCommandInfo(String commandName, CommandInfo info) {
        this.commandInfo.put(commandName, info);
    }

    @Override
    public void registerChatCommand(String commandExecutor, BiConsumer<BaseSession, String[]> consumer) {
        this.chatCommands.put(commandExecutor, consumer);
    }

    @Override
    public void registerEvent(Event consumer) {
        if (this.listeners.containsKey(consumer.getClass())) {
            this.listeners.get(consumer.getClass()).add(consumer);
        } else {
            this.listeners.put(consumer.getClass(), Lists.newArrayList(consumer));
        }

        log.debug(String.format("Registered event listener for %s", consumer.getClass().getSimpleName()));
    }

    @Override
    public <T extends EventArgs> boolean handleEvent(Class<? extends Event> eventClass, T args) {
        if (this.listeners.containsKey(eventClass)) {
            this.invoke(eventClass, args);
            log.debug(String.format("Event handled: %s\n", eventClass.getSimpleName()));
        } else {
            log.debug(String.format("Unhandled event: %s\n", eventClass.getSimpleName()));
        }

        return args.isCancelled();
    }

    @Override
    public Map<String, CommandInfo> getCommands() {
        return this.commandInfo;
    }

    @Override
    public boolean handleCommand(BaseSession session, String commandExectutor, String[] arguments) {
        if(!this.chatCommands.containsKey(commandExectutor) || !this.commandInfo.containsKey(commandExectutor)) {
            return false;
        }

        CommandInfo commandInfo = this.commandInfo.get(commandExectutor);

        if(!session.getPlayer().getPermissions().hasCommand(commandInfo.getPermission()) && !commandInfo.getPermission().isEmpty()) {
            return false;
        }

        BiConsumer<BaseSession, String[]> chatCommand = this.chatCommands.get(commandExectutor);

        try {
            chatCommand.accept(session, arguments);
        } catch(Exception e) {
            log.warn("Failed to execute module command: " + commandExectutor);
        }

        return true;
    }

    private  <T extends EventArgs> void invoke(Class<? extends Event> eventClass, T args) {
        for (Event event : this.listeners.get(eventClass)) {
            try {
                if(event.isAsync()) {
                    this.asyncEventExecutor.submit(() -> {
                       event.consume(args);
                    });
                } else {
                    event.consume(args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}