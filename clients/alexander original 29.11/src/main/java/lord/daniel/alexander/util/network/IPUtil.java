package lord.daniel.alexander.util.network;

import lord.daniel.alexander.util.json.JSONUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.swing.text.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class IPUtil {

    public static String getCountry(String response) {
        return JSONUtil.getJsonValue(response, "country");
    }

    public static String getIp(String response) {
        return JSONUtil.getJsonValue(response, "ip");
    }


    public static String getLoc(String response) {
        return JSONUtil.getJsonValue(response, "loc");
    }

    public static String getCountry() {
        return JSONUtil.getJsonValue(getIPInfo(), "country");
    }

    public static String getLoc() {
        return JSONUtil.getJsonValue(getIPInfo(), "loc");
    }

    public static String getIPInfo() {
        try {
            URL url = new URL("https://ipinfo.io/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String response = content.toString();

            return response.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}
