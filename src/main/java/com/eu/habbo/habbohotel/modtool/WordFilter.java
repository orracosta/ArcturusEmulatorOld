package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.friends.FriendChatMessageComposer;
import com.eu.habbo.plugin.events.users.UserTriggerWordFilterEvent;
import com.eu.habbo.threading.runnables.InsertModToolIssue;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.text.Normalizer;

public class WordFilter
{
    //Configuration. Loaded from database & updated accordingly.
    public static boolean ENABLED_FRIENDCHAT = true;

    THashSet<WordFilterWord> autoReportWords = new THashSet<WordFilterWord>();
    THashSet<WordFilterWord> hideMessageWords = new THashSet<WordFilterWord>();
    THashSet<WordFilterWord> words = new THashSet<WordFilterWord>();

    public WordFilter()
    {
        long start = System.currentTimeMillis();
        this.reload();
        Emulator.getLogging().logStart("WordFilter -> Loaded! (" + (System.currentTimeMillis() - start) + " MS)");
    }

    public synchronized void reload()
    {
        if(!Emulator.getConfig().getBoolean("hotel.wordfilter.enabled"))
            return;

        this.autoReportWords.clear();
        this.hideMessageWords.clear();
        this.words.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement();)
        {
            try (ResultSet set = statement.executeQuery("SELECT * FROM wordfilter"))
            {
                while (set.next())
                {
                    WordFilterWord word;

                    try
                    {
                        word = new WordFilterWord(set);
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                        continue;
                    }

                    if (word.autoReport)
                        this.autoReportWords.add(word);
                    else if (word.hideMessage)
                        this.hideMessageWords.add(word);
                    else
                        words.add(word);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Normalises a string to replace all weird unicode characters to regular characters.
     * May dispose certain characters (Like Chinese)
     * @param message The {@String} to normalise.
     * @return A String with only regular characters.
     */
    public String normalise(String message)
    {
        return Normalizer.normalize(message, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").replaceAll("\\p{M}", "").replace("1", "i").replace("2", "z").replace("3", "e").replace("4","a").replace("5", "s").replace("8", "b").replace("0", "o");
    }


    public boolean autoReportCheck(RoomChatMessage roomChatMessage)
    {
        String message = this.normalise(roomChatMessage.getMessage()).toLowerCase();

        TObjectHashIterator iterator = this.autoReportWords.iterator();

        while (iterator.hasNext())
        {
            WordFilterWord word = (WordFilterWord) iterator.next();

            if (message.contains(word.key))
            {
                Emulator.getGameEnvironment().getModToolManager().quickTicket(roomChatMessage.getHabbo(), "Automatic WordFilter", roomChatMessage.getMessage());

                if(Emulator.getConfig().getBoolean("notify.staff.chat.auto.report"))
                {
                    Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(new FriendChatMessageComposer(new Message(roomChatMessage.getHabbo().getHabboInfo().getId(), 0, Emulator.getTexts().getValue("warning.auto.report").replace("%user%", roomChatMessage.getHabbo().getHabboInfo().getUsername()).replace("%word%", word.key))).compose(), "acc_staff_chat");
                }
                return true;
            }
        }

        return false;
    }

    public boolean hideMessageCheck(String message)
    {
        message = this.normalise(message).toLowerCase();

        TObjectHashIterator iterator = this.hideMessageWords.iterator();

        while (iterator.hasNext())
        {
            WordFilterWord word = (WordFilterWord) iterator.next();

            if (message.contains(word.key))
            {
                return true;
            }
        }

        return false;
    }

    public String[] filter(String[] messages)
    {
        for(int i = 0; i < messages.length; i++)
        {
            messages[i] = this.filter(messages[i], null);
        }

        return messages;
    }

    public String filter(String message, Habbo habbo)
    {
        String original = message;

        if(Emulator.getConfig().getBoolean("hotel.wordfilter.normalise"))
        {
            message = this.normalise(message);
        }

        TObjectHashIterator iterator = this.words.iterator();

        boolean foundShit = false;

        while(iterator.hasNext())
        {
            WordFilterWord word = (WordFilterWord) iterator.next();

            if(message.contains(word.key))
            {
                if(habbo != null)
                {
                    if(Emulator.getPluginManager().fireEvent(new UserTriggerWordFilterEvent(habbo, word)).isCancelled())
                        continue;
                }
                message = message.replace(word.key, word.replacement);
                foundShit = true;

                if (word.muteTime > 0)
                {
                    habbo.mute(word.muteTime);
                }
            }
        }

        if (!foundShit)
        {
            return original;
        }

        return message;
    }

    public void filter (RoomChatMessage roomChatMessage, Habbo habbo)
    {
        String message = roomChatMessage.getMessage().toLowerCase();

        if(Emulator.getConfig().getBoolean("hotel.wordfilter.normalise"))
        {
            message = this.normalise(message);
        }

        TObjectHashIterator iterator = this.words.iterator();

        while(iterator.hasNext())
        {
            WordFilterWord word = (WordFilterWord) iterator.next();

            if(message.contains(word.key))
            {
                if(habbo != null)
                {
                    if(Emulator.getPluginManager().fireEvent(new UserTriggerWordFilterEvent(habbo, word)).isCancelled())
                        continue;
                }

                message = message.replace(word.key, word.replacement);
                roomChatMessage.filtered = true;
            }
        }

        if (roomChatMessage.filtered)
        {
            roomChatMessage.setMessage(message);
        }
    }
}
