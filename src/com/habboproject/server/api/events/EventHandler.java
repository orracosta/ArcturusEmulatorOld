package com.habboproject.server.api.events;


import com.habboproject.server.api.commands.CommandInfo;
import com.habboproject.server.api.networking.sessions.BaseSession;

import java.util.Map;
import java.util.function.BiConsumer;

public interface EventHandler  {
   void initialize();

   <T extends EventArgs> boolean handleEvent(Class<? extends Event> eventClass, T args);

   void registerEvent(Event consumer);

   void registerChatCommand(String commandExecutor, BiConsumer<BaseSession, String[]> consumer);

   void registerCommandInfo(String commandName, CommandInfo info);

   Map<String, CommandInfo> getCommands();

   boolean handleCommand(BaseSession session, String commandExectutor, String[] arguments);
}
