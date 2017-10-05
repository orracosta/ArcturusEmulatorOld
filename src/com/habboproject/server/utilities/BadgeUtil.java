package com.habboproject.server.utilities;

import java.util.List;


public class BadgeUtil {
    private static String format(int num) {
        return (num < 10 ? "0" : "") + num;
    }

    public static String generate(int guildBase, int guildBaseColor, List<Integer> guildStates) {
        String badgeImage = "b" + format(guildBase) + "" + format(guildBaseColor);

        for (int i = 0; i < 3 * 4; i += 3) {
            badgeImage += i >= guildStates.size() ? "s" : "s" + format(guildStates.get(i)) + format(guildStates.get(i + 1)) + "" + guildStates.get(i + 2);
        }

        return badgeImage;
    }
}
