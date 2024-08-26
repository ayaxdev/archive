package com.skidding.atlas.util.system;

import com.skidding.atlas.AtlasClient;
import lombok.experimental.UtilityClass;

import java.awt.*;
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

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            AtlasClient.getInstance().logger.error(STR."Could not read input stream \{inputStream}");
        }

        return stringBuilder.toString();
    }

    public static File getFileFromDialog() {
        FileDialog fileDialog = new FileDialog((Frame) null, "Select a .txt file");
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setFile("*.txt");

        fileDialog.setVisible(true);
        fileDialog.setAlwaysOnTop(true);
        String fileName = fileDialog.getFile();
        if (fileName == null) {
            return null;
        }

        return new File(fileDialog.getDirectory(), fileName);
    }

}
