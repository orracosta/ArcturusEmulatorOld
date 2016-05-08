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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;

public class WordFilter
{
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

        this.words.add(new WordFilterWord("azure ", "poop "));

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM wordfilter");
            ResultSet set = statement.executeQuery();

            while(set.next())
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

                if(word.autoReport)
                    this.autoReportWords.add(word);
                else if(word.hideMessage)
                    this.hideMessageWords.add(word);
                else
                    words.add(word);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
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
                .replaceAll("[^\\p{ASCII}]", "");
    }


    public boolean autoReportCheck(RoomChatMessage roomChatMessage)
    {
        String message = this.normalise(roomChatMessage.getMessage());

        TObjectHashIterator iterator = this.autoReportWords.iterator();

        while (iterator.hasNext())
        {
            WordFilterWord word = (WordFilterWord) iterator.next();

            if (message.contains(word.key))
            {
                Emulator.getGameEnvironment().getModToolManager().quickTicket(roomChatMessage.getHabbo(), "Automatic WordFilter", roomChatMessage.getMessage());

                if(Emulator.getConfig().getBoolean("notify.staff.chat.auto.report"))
                {
                    Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(new FriendChatMessageComposer(new Message(0, 0, Emulator.getTexts().getValue("warning.auto.report").replace("%user%", roomChatMessage.getHabbo().getHabboInfo().getUsername()).replace("%word%", word.key))).compose(), "acc_staff_chat");
                }
                return true;
            }
        }

        return false;
    }

    public boolean hideMessageCheck(String message)
    {
        message = this.normalise(message);

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
            }
        }

        return message;
    }
}
