package com.habboproject.server.api.transformers;

import com.habboproject.server.utilities.JsonFactory;
import spark.ResponseTransformer;


public class JsonTransformer implements ResponseTransformer {
    /**
     * Render the template as JSON using the GSON instance
     *
     * @param o The object which we need to transform into JSON format
     * @return JSON formatted string
     * @throws Exception
     */
    @Override
    public String render(Object o) throws Exception {
        try {
            String gsonString = JsonFactory.getInstance().toJson(o);

            if (!gsonString.startsWith("{")) {
                return "{\"response\":" + gsonString + "}";
            } else {
                return gsonString;
            }
        } catch (Exception e) {
            return JsonFactory.getInstance().toJson(e);
        } finally {
            // Dispose object..
        }
    }
}
