package com.skidding.atlas.font;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.util.network.WebUtil;
import com.skidding.atlas.util.system.FileUtil;
import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.*;

public class FontManager extends Manager<ClientFontRenderer> {

    private static volatile FontManager fontManager;

    public static synchronized FontManager getSingleton() {
        if(fontManager == null)
            fontManager = new FontManager();

        return fontManager;
    }

    public final HashMap<String, List<String>> available = new LinkedHashMap<>();
    private File fontDirectory;

    @Override
    public void preMinecraftLaunch() {
        // Set up the font directory in client directory and version file in font directory,
        fontDirectory = new File(AtlasClient.getInstance().directory, "fonts");
        final File versionFile = new File(fontDirectory, "version.txt");

        // Check if the font directory exists, but is not a directory and if so, delete it
        if(fontDirectory.exists() && !fontDirectory.isDirectory())
            try {
                Files.delete(fontDirectory.toPath());
            } catch (Exception e) {
                throw new RuntimeException("Cannot delete the file in place of the font directory", e);
            }

        if(versionFile.exists() && !versionFile.isFile())
            try {
                Files.delete(versionFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException("Cannot delete the folder in place of the version file", e);
            }

        // Check if the fonts need to be updated
        boolean needUpdate = !fontDirectory.exists() || !versionFile.exists();

        // Get the latest version
        String latestVersion;

        try {
            latestVersion = WebUtil.sendGetRequest("https://atlasclient.vercel.app/api/client-assets/fonts/version");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Cannot get the latest fonts version", e);
        }

        // Get and compare the current version
        if(!needUpdate) {
            try {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(versionFile))) {
                    String currentVersion = bufferedReader.readLine();
                    if(!currentVersion.equals(latestVersion))
                        needUpdate = true;
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot read the version file", e);
            }
        }

        if(!needUpdate)
            AtlasClient.getInstance().logger.info(STR."Fonts are up-to-date (\{versionFile.getAbsolutePath()})");

        // If the font directory does not exist, download the font archive stored on the cloud
        if(needUpdate) {
            // Check if the font directory exists, and if so, delete it
            if(fontDirectory.exists()) {
                try {
                    for(File file : Objects.requireNonNull(fontDirectory.listFiles())) {
                        if(file.isDirectory())
                            FileUtils.deleteDirectory(file);
                        else {
                            Files.delete(file.toPath());
                        }
                    }

                    FileUtils.deleteDirectory(fontDirectory);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot delete the fonts in font directory", e);
                }
            }

            // Create the font directory, and throw an exception if it fails
            if(!fontDirectory.mkdirs())
                throw new RuntimeException("Cannot create font directory");

            try {
                // Get the list of fonts
                String fontList;
                try {
                    fontList = WebUtil.sendGetRequest("https://atlasclient.vercel.app/api/client-assets/fonts/list");
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException("Cannot get the list of fonts", e);
                }

                // Get the list of fonts
                final String[] fonts = fontList.split(", ");

                for(String font : fonts) {
                    // Download the font file
                    File downloadedFile = WebUtil.downloadFile(STR."https://atlasclient.vercel.app/api/client-assets/fonts/get/\{font.replace(" ", "-")}", new File(fontDirectory, STR."\{font}.zip").getCanonicalPath());
                    // Set the file to be deleted once the client stops
                    downloadedFile.deleteOnExit();

                    // Creating the font directory
                    final File curFontDirectory = new File(fontDirectory, font);
                    if(!curFontDirectory.exists())
                        if(!curFontDirectory.mkdirs())
                            throw new RuntimeException(STR."Cannot create the \{font} directory");

                    // Check if the download file exists, and if so unpack it
                    if(downloadedFile.exists()) {
                        final Unzip unzip = new Unzip();
                        unzip.setSrc(downloadedFile);
                        unzip.setDest(curFontDirectory);
                        unzip.execute();
                    } else {
                        // Throw an exception if the downloaded file does not exist
                        throw new RuntimeException("Downloaded font file does not exist");
                    }
                }
            } catch (IOException exception) {
                throw new RuntimeException("Input/Output exception when downloading and unpacking Fonts.zip:", exception);
            }

            // Create the version file and write the latest version
            try {
                if(!versionFile.createNewFile())
                    throw new RuntimeException("Cannot create version file");

                try (FileWriter fileWriter = new FileWriter(versionFile)) {
                    fileWriter.write(Objects.requireNonNull(latestVersion));
                }
            } catch (IOException ioException) {
                throw new RuntimeException("Cannot create or write to version file");
            }

        }

        // Get the list of fonts
        for(File file : Objects.requireNonNull(fontDirectory.listFiles())) {
            // Continue if the file is not a directory
            if(!file.isDirectory())
                continue;

            final String fontName = file.getName();
            final List<String> types = new ArrayList<>();

            // Get the list of font types
            for(File font : Objects.requireNonNull(file.listFiles())) {
                // Continue if the file is not a font
                if(!font.getName().endsWith(".ttf") && !font.getName().endsWith(".otf"))
                    continue;

                // Add the font type
                types.add(font.getName().split("\\.")[0]);
            }

            // Add the font to the list
            this.available.put(fontName, types);
        }
    }

    public ClientFontRenderer add(String family, String type, float size) throws IOException, FontFormatException {
        // Rounding size
        size = Math.round(size);
        // Create the file object for the family directory
        final File familyDirectory = new File(fontDirectory, family);
        // Create the directory if it does not exist
        if (FileUtil.checkAndCreateDirectory(familyDirectory)) {
            // Set up file objects for possible .ttf font and .otf font
            final File ttfFile = new File(familyDirectory, STR."\{type}.ttf");
            final File otfFile = new File(familyDirectory, STR."\{type}.otf");
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
                AtlasClient.getInstance().logger.error(STR."There was an error finding the font \{ family } \{ type } \{ size }");
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
            if(this.map.containsKey(name)) {
                return this.map.get(name);
            } else {
                try {
                    AtlasClient.getInstance().logger.info(STR. "Font \{ family }-\{ type } \{ size } does not exist, creating now" );
                    return add(family, type, size);
                } catch (Exception e2) {
                    throw new RuntimeException(STR. "Failed to create font \{ family }-\{ type } \{ size }", e2);
                }
            }
        } catch (Exception e) {
            AtlasClient.getInstance().logger.error("There was an error iterating over font map:", e);
            return null;
        }
    }

}
