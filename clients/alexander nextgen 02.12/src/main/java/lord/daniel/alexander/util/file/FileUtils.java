package lord.daniel.alexander.util.file;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Written by Daniel. on 30/10/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@UtilityClass
public class FileUtils {

    public ArrayList<File> listFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        ArrayList<File> fileList = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                fileList.addAll(Arrays.asList(files));
            }
        }

        return fileList;
    }

    public static List<String> listFilesInResources(String folder) throws IOException {
        List<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(getResourceFolderAsPath(folder))) {
            for (Path path : directoryStream) {
                fileList.add(path.getFileName().toString());
            }
        }
        return fileList;
    }

    private static Path getResourceFolderAsPath(String folder) throws IOException {
        try {
            Path path = Paths.get(FileUtils.class.getClassLoader().getResource(folder).toURI());

            if (!Files.exists(path) || !Files.isDirectory(path)) {
                throw new IOException("Invalid folder path: " + folder);
            }

            return path;
        } catch (URISyntaxException e) {
            throw new IOException("Failed to convert URL to URI", e);
        } catch (NullPointerException e) {
            throw new IOException("Resource is null", e);
        }
    }



}
