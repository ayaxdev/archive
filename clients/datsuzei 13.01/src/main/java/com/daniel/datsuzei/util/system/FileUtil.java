package com.daniel.datsuzei.util.system;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

@UtilityClass
public class FileUtil {

    public boolean checkAndCreateDirectory(File file) {
        if(file.exists()) {
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

}
