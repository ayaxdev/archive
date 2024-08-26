package com.atani.nextgen.util.system;

import com.atani.nextgen.AtaniClient;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

@UtilityClass
public class FileUtil {

    public boolean checkAndCreateDirectory(File file) {
        if (file.exists()) {
            return file.isDirectory();
        } else {
            return file.mkdirs();
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

    public static String readFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            AtaniClient.getInstance().logger.error(STR."Could not read file \{file.getName()}");
        }

        return stringBuilder.toString();
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            AtaniClient.getInstance().logger.error(STR."Could not read input stream \{inputStream}");
        }

        return stringBuilder.toString();
    }

}
