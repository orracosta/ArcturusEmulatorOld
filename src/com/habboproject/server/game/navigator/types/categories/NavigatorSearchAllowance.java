package com.habboproject.server.game.navigator.types.categories;

public enum NavigatorSearchAllowance {
    NOTHING,
    SHOW_MORE,
    GO_BACK;

    public static int getIntValue(NavigatorSearchAllowance allowance) {
        switch(allowance) {
            default:
            case NOTHING:
                return 0;
            case SHOW_MORE:
                return 1;
            case GO_BACK:
                return 2;
        }
    }
}
