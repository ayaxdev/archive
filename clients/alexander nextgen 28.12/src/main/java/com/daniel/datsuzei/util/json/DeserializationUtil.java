package com.daniel.datsuzei.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeserializationUtil {

    public static JsonElement elementExists(String name, JsonObject jsonObject) throws ElementNotFoundException {
        if(jsonObject.has(name)) {
            return jsonObject.get(name);
        } else {
            throw new ElementNotFoundException(name);
        }
    }

    public static class ElementNotFoundException extends RuntimeException {

        public ElementNotFoundException(String elementName) {
            super(STR."Json possibly invalid, missing element \{elementName}");
        }
    }

}
