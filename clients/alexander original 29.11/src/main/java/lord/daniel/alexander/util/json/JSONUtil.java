package lord.daniel.alexander.util.json;

import org.json.JSONObject;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class JSONUtil {

    public static String getJsonValue(JSONObject jsonObject, String entry) {
        try {
            // Get the value for the given entry/key
            if (jsonObject.has(entry)) {
                return jsonObject.getString(entry);
            } else {
                return "Key not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public static String getJsonValue(String json, String entry) {
        try {
            // Parse the input JSON string
            JSONObject jsonObject = new JSONObject(json);

            // Get the value for the given entry/key
            if (jsonObject.has(entry)) {
                return jsonObject.getString(entry);
            } else {
                return "Key not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}
