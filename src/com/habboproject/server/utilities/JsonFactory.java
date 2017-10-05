package com.habboproject.server.utilities;

import com.google.gson.Gson;


public class JsonFactory {
    private static final Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }
}
