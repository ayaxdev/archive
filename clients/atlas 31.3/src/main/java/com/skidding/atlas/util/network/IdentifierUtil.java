package com.skidding.atlas.util.network;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.util.parse.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class IdentifierUtil {

    private final String response;

    public IdentifierUtil() {
        response = getIPInfo();
    }

    public String getCountry() {
        return JSONUtil.quickParse(response, "country");
    }

    public String getLoc() {
        return JSONUtil.quickParse(response, "loc");
    }

    public static String getIPInfo() {
        try {
            final URL url = new URI("https://ipinfo.io/").toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                String response = content.toString();

                return response.trim();
            } catch (Exception e) {
                AtlasClient.getInstance().logger.error("Failed to parse response", e);
            }
        } catch (Exception e) {
            AtlasClient.getInstance().logger.error("Failed to connect to ip API", e);
        }

        AtlasClient.getInstance().logger.error("Failed to retrieve IP");

        return null;
    }

}
