package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitUpdateUsernameComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.plugin.events.bots.BotSavedChatEvent;
import com.eu.habbo.plugin.events.bots.BotSavedLookEvent;
import com.eu.habbo.plugin.events.bots.BotSavedNameEvent;
import org.jsoup.Jsoup;

import java.util.ArrayList;

public class BotSaveSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            int botId = this.packet.readInt();

            Bot bot = room.getBot(Math.abs(botId));

            if(bot == null)
                return;

            int settingId = this.packet.readInt();

            switch(settingId)
            {
                case 1:
                    BotSavedLookEvent lookEvent = new BotSavedLookEvent(bot,
                                                                        this.client.getHabbo().getHabboInfo().getGender(),
                                                                        this.client.getHabbo().getHabboInfo().getLook(),
                                                                        this.client.getHabbo().getRoomUnit().getEffectId());
                    Emulator.getPluginManager().fireEvent(lookEvent);

                    if(lookEvent.isCancelled())
                        break;

                    bot.setFigure(lookEvent.newLook);
                    bot.setGender(lookEvent.gender);
                    bot.setEffect(lookEvent.effect);
                    bot.needsUpdate(true);
                    break;

                case 2:
                    String messageString = this.packet.readString();

                    if(messageString.length() > 5112)
                        break;

                    String[] data = messageString.split(";#;");

                    ArrayList<String> chat = new ArrayList<String>();
                    for(int i = 0; i < data.length - 3; i++)
                    {
                        for(String s : data[i].split("\r"))
                        {
                            chat.add(Emulator.getGameEnvironment().getWordFilter().filter(Jsoup.parse(s).text(), this.client.getHabbo()));
                        }
                    }

                    int chatSpeed = Integer.valueOf(data[data.length -2]);
                    if (chatSpeed < BotManager.MINIMUM_CHAT_SPEED)
                    {
                        chatSpeed = BotManager.MINIMUM_CHAT_SPEED;
                    }

                    BotSavedChatEvent chatEvent = new BotSavedChatEvent(bot, Boolean.valueOf(data[data.length - 3]), Boolean.valueOf(data[data.length - 1]), chatSpeed, chat);
                    Emulator.getPluginManager().fireEvent(chatEvent);

                    if(chatEvent.isCancelled())
                        break;

                    bot.setChatAuto(chatEvent.autoChat);
                    bot.setChatRandom(chatEvent.randomChat);
                    bot.setChatDelay((short) chatEvent.chatDelay);
                    bot.clearChat();
                    bot.addChatLines(chat);
                    bot.needsUpdate(true);
                    break;

                case 3:
                    bot.getRoomUnit().setCanWalk(!bot.getRoomUnit().canWalk());
                    bot.needsUpdate(true);
                    break;

                case 4:
                    bot.getRoomUnit().setDanceType(DanceType.values()[(bot.getRoomUnit().getDanceType().getType() + 1) %DanceType.values().length]);
                    room.sendComposer(new RoomUserDanceComposer(bot.getRoomUnit()).compose());
                    bot.needsUpdate(true);
                    break;

                case 5:
                    BotSavedNameEvent nameEvent = new BotSavedNameEvent(bot, Jsoup.parse(this.packet.readString()).text());

                    if(nameEvent.name.length() <= 25)
                    {
                        Emulator.getPluginManager().fireEvent(nameEvent);

                        if (nameEvent.isCancelled())
                            break;

                        bot.setName(nameEvent.name);
                        bot.needsUpdate(true);
                        room.sendComposer(new RoomUnitUpdateUsernameComposer(bot.getRoomUnit(), nameEvent.name).compose());
                    }
                    break;
            }

            if(bot.needsUpdate())
            {
                Emulator.getThreading().run(bot);
            }
        }
    }
}
