package com.skidding.atlas.util.parse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JSONUtil {

    public static String quickParse(String json, String entry) throws JsonParseException {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has(entry)) {
            return jsonObject.get(entry).getAsString();
        } else {
            return "Key not found";
        }
    }

}