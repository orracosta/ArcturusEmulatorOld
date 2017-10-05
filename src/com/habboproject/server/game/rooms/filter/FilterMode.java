package com.habboproject.server.game.rooms.filter;

public enum FilterMode {
    /*
        Smart filter mode will convert things such as accents in the text to a readable character by the filter,
        so the players cannot bypass it this way.

        Strict will use the same method of filter as smart but will not send the message, instead it will show the
        player who sent the triggering message an alert.
     */

    STRICT,
    SMART,
    DEFAULT
}