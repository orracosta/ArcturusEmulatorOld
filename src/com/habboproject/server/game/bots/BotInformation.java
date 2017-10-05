package com.habboproject.server.game.bots;

public interface BotInformation {
    /**
     * Get the name of the bot
     *
     * @return The name of the bot
     */
    public String getUsername();

    /**
     * Get the motto of the bot
     *
     * @return The motto of the bot
     */
    public String getMotto();

    /**
     * Get the figure of the bot
     *
     * @return The figure of the bot
     */
    public String getFigure();

    /**
     * Get the gender of the bot
     *
     * @return The gender of the bot
     */
    public String getGender();
}
