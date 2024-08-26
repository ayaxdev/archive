package com.atani.nextgen.font;


import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.feature.Manager;
import com.atani.nextgen.util.system.FileUtil;
import org.apache.ant.compress.taskdefs.Unzip;

import java.awt.*;
import java.io.*;

public class FontManager extends Manager<ClientFontRenderer> {

    private static volatile FontManager fontManager;

    public static FontManager getSingleton() {
        if(fontManager == null)
            fontManager = new FontManager();

        return fontManager;
    }

    private File fontDirectory;

    @Override
    public void preMinecraftLaunch() {
        // Set up the font directory in client directory
        fontDirectory = new File(AtaniClient.getInstance().directory, "Fonts");

        // Check if the font directory exists, but is not a directory and if so delete it
        if(fontDirectory.exists() && !fontDirectory.isDirectory())
            if(!fontDirectory.delete())
                throw new RuntimeException(STR."Cannot delete the file in place of the font directory");

        // If the font directory does not exist, download the font archive stored on cloud
        if(!fontDirectory.exists()) {
            // Create the font directory, and throw an exception if it fails
            if(!fontDirectory.mkdirs())
                throw new RuntimeException(STR."Cannot create font directory");

            try {
                // Download the fonts file
                File downloadedFile = FileUtil.downloadFile("https://github.com/Atani-NextGen/cloud/raw/main/Fonts.zip", new File(fontDirectory, "Fonts.zip").getCanonicalPath());
                // Set the file to be deleted once the client stops
                downloadedFile.deleteOnExit();

                // Check if the download file exists, and if so unpack it
                if(downloadedFile.exists()) {
                    final Unzip unzip = new Unzip();
                    unzip.setSrc(downloadedFile);
                    unzip.setDest(fontDirectory);
                    unzip.execute();
                } else {
                    // Throw an exception if the downloaded file does not exist
                    throw new RuntimeException("Downloaded fonts file does not exist");
                }
            } catch (IOException exception) {
                AtaniClient.getInstance().logger.error("Input/Output exception when downloading and unpacking Fonts.zip:", exception);
            }
        }
    }

    public ClientFontRenderer add(String family, String type, float size) throws IOException, FontFormatException {
        // Create the file object for the family directory
        final File familyDirectory = new File(fontDirectory, family);
        // Create the directory if it does not exist
        if (FileUtil.checkAndCreateDirectory(familyDirectory)) {
            // Set up file objects for possible .ttf font and .otf font
            final File ttfFile = new File(familyDirectory, type + ".ttf");
            final File otfFile = new File(familyDirectory, type + ".otf");
            // Check which file type exists, and if none exist, throw an exception
            File fontFile;
            if (ttfFile.exists())
                fontFile = ttfFile;
            else if (otfFile.exists())
                fontFile = otfFile;
            else
                throw new FileNotFoundException("Font does not exist!");
            // Create the input stream for the existing file
            try (InputStream in = new FileInputStream(fontFile.getAbsolutePath())) {
                // Create the font object from the input stream, and derive it from the selected size
                final Font finalFont = Font.createFont(Font.PLAIN, in).deriveFont(size);
                // Create the client font renderer object
                final ClientFontRenderer finalFontRenderer = new ClientFontRenderer(family, type, size, finalFont, false);
                // Add the created font renderer to the map
                this.add(finalFontRenderer.getName(), finalFontRenderer);
                // Return the final font renderer
                return finalFontRenderer;
            } catch (Exception e) {
                // Throw an error if there was a problem creating the input stream
                AtaniClient.getInstance().logger.error(STR."There was an error finding the font \{ family } \{ type } \{ size }");
                throw new RuntimeException(e);
            }
        } else {
            // Throw an error if there was a problem creating the font directory
            throw new RuntimeException(STR."Failed to create the font directory \{familyDirectory.getAbsolutePath()}");
        }
    }

    // Gets the Regular font from the family
    public ClientFontRenderer get(String family, float size) {
        return this.get(family, "Regular", size);
    }

    // Gets a font from the map, and if it does not exist attempt to create it
    public ClientFontRenderer get(String family, String type, float size) {
        try {
            String name = STR."\{family}-\{type}-\{size}";
            if(this.map.containsKey(name.toLowerCase())) {
                return this.map.get(name.toLowerCase());
            } else {
                try {
                    AtaniClient.getInstance().logger.info(STR. "Font \{ family }-\{ type } \{ size } does not exist, creating now" );
                    return add(family, type, size);
                } catch (Exception e2) {
                    AtaniClient.getInstance().logger.error(STR. "Failed to create font \{ family }-\{ type } \{ size }:" , e2);
                    return null;
                }
            }
        } catch (Exception e) {
            AtaniClient.getInstance().logger.error("There was an error iterating over font map:", e);
            return null;
        }
    }

}
