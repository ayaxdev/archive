package com.skidding.atlas.util.network;

import com.skidding.atlas.AtlasClient;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.*;

@UtilityClass
public class WebUtil {

    public String sendGetRequest(String path) throws IOException, URISyntaxException {
        final URL url = new URI(path).toURL();
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        final int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                final StringBuilder response = new StringBuilder();

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            } catch (Exception e) {
                throw new IOException("Failed to read response", e);
            }
        } else {
            throw new IOException(STR."GET request failed with response code: \{responseCode}");
        }
    }

    public static File downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = URI.create(fileUrl).toURL();
        URLConnection connection = url.openConnection();

        try (InputStream in = connection.getInputStream()) {
            File destinationFile = new File(destinationPath);
            try (FileOutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                return destinationFile;
            }
        }
    }

}
