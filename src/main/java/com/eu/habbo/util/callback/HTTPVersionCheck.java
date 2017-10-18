package com.eu.habbo.util.callback;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPVersionCheck implements Runnable
{
    private void sendPost()
    {
        try
        {
            if (!Emulator.isReady)
                return;

            String url = "http://arcturus.wf/callback/check.php";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "arcturus");
            String urlParameters = "&major=" + Emulator.MAJOR + "&minor=" + Emulator.MINOR + "&build=" + Emulator.BUILD + "&version=" + Emulator.version + "&users=" + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "&rooms=" + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "&username=" + Emulator.getConfig().getValue("username");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 102)
            {
                String text = "";
                InputStreamReader in = new InputStreamReader((InputStream) con.getContent());
                BufferedReader buff = new BufferedReader(in);
                String line;
                do {
                    line = buff.readLine();

                    if (line != null)
                    {
                        text += (line + "\n");
                    }
                } while (line != null);
                buff.close();
                in.close();

                Emulator.getLogging().logStart(text);
                Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GenericAlertComposer(text));
            }
            wr.close();
            con.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void run()
    {
//        try
//        {
//            this.sendPost();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        Emulator.getThreading().run(this, 1000);
    }
}
