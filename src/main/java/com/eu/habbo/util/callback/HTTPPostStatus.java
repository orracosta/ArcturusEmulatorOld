package com.eu.habbo.util.callback;

import com.eu.habbo.Emulator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created on 24-12-2015 13:03.
 */
public class HTTPPostStatus implements Runnable
{
    private void sendPost() throws Exception {

        String url = "http://arcturus.wf/callback/status.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "arcturus");

        String urlParameters = "users=" + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "&rooms=" + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "&username=" + Emulator.getConfig().getValue("username");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();

//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();

        //print result
        con.disconnect();
        return;
    }

    @Override
    public void run()
    {
        try
        {
            this.sendPost();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}