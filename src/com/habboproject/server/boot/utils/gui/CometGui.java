package com.habboproject.server.boot.utils.gui;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.utilities.CometStats;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CometGui extends JFrame {
    private JPanel mainPanel;
    private JLabel playersOnline;
    private JLabel roomsLoaded;
    private JLabel uptime;

    public CometGui() {
        super("Comet Server - " + Comet.getBuild());

        this.pack();

        if(mainPanel == null) {
            return;
        }

        this.setContentPane(mainPanel);

        this.setSize(new Dimension(350, 120));
        this.setResizable(false);

        ThreadManager.getInstance().executePeriodic(this::update, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void update() {
        final CometStats stats = CometStats.get();

        playersOnline.setText("Players online: " + stats.getPlayers());
        roomsLoaded.setText("Rooms loaded: " + stats.getRooms());
        uptime.setText("Uptime: " + stats.getUptime());
    }
}
