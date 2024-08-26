package lord.daniel.alexander.util.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class APIUtil {

    public static JSONObject readJsonFromUrl(String link) throws IOException, JSONException {
        // Input Stream Object To Start Streaming.
        try (InputStream input = new URL(link).openStream()) { // try catch for checked exception
            BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
            // Buffer Reading In UTF-8
            String Text = read(re); // Handy Method To Read Data From BufferReader
            return new JSONObject(Text); // Returning JSON
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String read(Reader re) throws IOException { // class Declaration
        StringBuilder str = new StringBuilder(); // To Store Url Data In String.
        int temp;
        do {
            temp = re.read(); // reading Charcter By Chracter.
            str.append((char) temp);

        } while (temp != -1);
        //  re.read() return -1 when there is end of buffer , data or end of file.

        return str.toString();
    }


}
