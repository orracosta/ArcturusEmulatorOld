package com.habboproject.server.game.bots;

import com.habboproject.server.storage.queries.bots.RoomBotDao;
import com.habboproject.server.utilities.JsonFactory;
import com.habboproject.server.utilities.RandomInteger;

import java.util.Arrays;

public abstract class BotData implements BotInformation {
    /**
     * The ID of the bot
     */
    private int id;

    /**
     * How long before the bot will chat in the room?
     */
    private int chatDelay;

    /**
     * The ID of the player who owns the bot
     */
    private int ownerId;

    /**
     * The name of the bot
     */
    private String username;

    /**
     * The motto of the bot
     */
    private String motto;

    /**
     * The figure of the bot
     */
    private String figure;

    /**
     * The gender of the bot
     */
    private String gender;

    /**
     * The name of the bot's owner
     */
    private String ownerName;

    /**
     * Can the bot talk without being triggered (currently the only way it can...)
     */
    private boolean isAutomaticChat;

    /**
     * The messages the bot can say
     */
    private String[] messages;

    private String botType;
    private String mode;

    private String data;

    private int forcedUserTargetMovement;
    private long forcedFurniTargetMovement;
    private boolean carryingItemToUser;

    /**
     * Initialize the bot
     *  @param id            The ID of the bot
     * @param username      The name of the bot
     * @param motto         The motto of the bot
     * @param figure        The figure of the bot
     * @param gender        The gender of the bot
     * @param ownerName     The name of the 1 owner
     * @param ownerId       The ID of the owner of the bot
     * @param messages      The messages the bot can say
     * @param automaticChat Can the bot talk without being triggered?
     * @param chatDelay     How long before the bot will next talk
     * @param botType
     * @param mode
     * @param data
     */
    public BotData(int id, String username, String motto, String figure, String gender, String ownerName, int ownerId, String messages, boolean automaticChat, int chatDelay, String botType, String mode, String data) {
        this.id = id;
        this.username = username;
        this.motto = motto;
        this.figure = figure;
        this.gender = gender;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.botType = botType;
        this.mode = mode;
        this.data = data;
        this.messages = (messages == null || messages.isEmpty()) ? new String[0] : JsonFactory.getInstance().fromJson(messages, String[].class);
        this.chatDelay = chatDelay;
        this.isAutomaticChat = automaticChat;
        this.forcedUserTargetMovement = 0;
        this.forcedFurniTargetMovement = 0;
        this.carryingItemToUser = false;
    }

    /**
     * Get a random chat message from the array
     *
     * @return A random chat message from the array
     */
    public String getRandomMessage() {
        if (this.getMessages().length > 0) {
            int index = RandomInteger.getRandom(0, (this.getMessages().length - 1));

            return this.stripNonAlphanumeric(this.getMessages()[index]);
        }

        return "";
    }

    /**
     * Strip non-alpha-numeric characters from a chat message
     *
     * @param msg The chat message we will filter
     * @return A filtered version of the chat message
     */
    private String stripNonAlphanumeric(String msg) {
        return msg.replace("<", "").replace(">", "");
    }

    /**
     * Save the bot data
     */
    public void save() {
        RoomBotDao.saveData(this);
    }

    /**
     * Get the bot's ID
     *
     * @return The bot's ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the bot's name
     *
     * @return The bot's name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the bot's username
     *
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = this.stripNonAlphanumeric(username);
    }

    /**
     * Get the bot's motto
     *
     * @return The bot's motto
     */
    public String getMotto() {
        return motto;
    }

    /**
     * Set the bot's motto
     *
     * @param motto The new motto
     */
    public void setMotto(String motto) {
        this.motto = motto;
    }

    /**
     * Get the bot's figure
     *
     * @return The bot's figure
     */
    public String getFigure() {
        return figure;
    }

    /**
     * Set the bot's figure
     *
     * @param figure The new figure
     */
    public void setFigure(String figure) {
        this.figure = figure;
    }

    /**
     * Get the bot's gender
     *
     * @return The bot's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set the bot's gender
     *
     * @param gender The new gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get how long it is before the bot will talk
     *
     * @return Seconds until the bot will talk
     */
    public int getChatDelay() {
        return this.chatDelay;
    }

    /**
     * Set how long it is before the bot can talk
     *
     * @param delay Seconds until the bot will talk
     */
    public void setChatDelay(int delay) {
        this.chatDelay = delay;
    }

    /**
     * Get the bot's chat messages
     *
     * @return Bot's chat messages
     */
    public String[] getMessages() {
        return this.messages;
    }

    /**
     * Set the bot's chat messages
     *
     * @param messages New chat messages
     */
    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    /**
     * Can the bot talk without being triggered?
     *
     * @return Whether or not the bot can talk without being triggered
     */
    public boolean isAutomaticChat() {
        return isAutomaticChat;
    }

    /**
     * Set whether or not the bot can talk without being triggered
     *
     * @param isAutomaticChat Whether or not the bot can talk without being triggered
     */
    public void setAutomaticChat(boolean isAutomaticChat) {
        this.isAutomaticChat = isAutomaticChat;
    }

    /**
     * Get the name of the owner of the bot
     *
     * @return The name of the owner of the bot
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Get the ID of the owner of the bot
     *
     * @return The ID of the owner of the bot
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Dispose the bot (Clear associated lists etc.)
     */
    public void dispose() {
        Arrays.fill(messages, null);
    }

    public String getBotType() {
        return botType;
    }

    public void setBotType(String botType) {
        this.botType = botType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getForcedUserTargetMovement() {
        return this.forcedUserTargetMovement;
    }

    public void setForcedUserTargetMovement(int forcedUserTargetMovement) {
        this.forcedUserTargetMovement = forcedUserTargetMovement;
    }

    public long getForcedFurniTargetMovement() {
        return this.forcedFurniTargetMovement;
    }

    public void setForcedFurniTargetMovement(long forcedFurniTargetMovement) {
        this.forcedFurniTargetMovement = forcedFurniTargetMovement;
    }

    public boolean isCarryingItemToUser() {
        return this.carryingItemToUser;
    }

    public void setCarryingItemToUser(boolean carryingItemToUser) {
        this.carryingItemToUser = carryingItemToUser;
    }
}

