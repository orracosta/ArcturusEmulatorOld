package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 17:37.
 */
public class SnowWarsOnGameEnding extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(1893);
        this.response.appendInt32(0); //idk

        //Game2GameResult
        this.response.appendBoolean(false);
        this.response.appendInt32(0); //resultType
        this.response.appendInt32(0); //result?

        this.response.appendInt32(1); //Count
        //{
            //Game2TeamScoreData
            this.response.appendInt32(1); //Team ID?
            this.response.appendInt32(100); //Score

            this.response.appendInt32(1); //Count
            //{
                //Game2TeamPlayerData
                this.response.appendString("Admin"); //username
                this.response.appendInt32(1); //UserID
                this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62"); //Look
                this.response.appendString("m"); //GENDER
                this.response.appendInt32(1337); //Score

                //Game2PlayerStatsData
                this.response.appendInt32(1337);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
            //}
        //}

        //Game2SnowWarGameStats
        this.response.appendInt32(1337);
        this.response.appendInt32(1338);

        return this.response;
    }
}
