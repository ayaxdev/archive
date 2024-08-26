package net.jezevcik.argon.file;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.ParekClient;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public abstract class DataFile {

    public final String name, folder;
    private final Path file;

    public DataFile(String name) {
        this(name, null);
    }

    public DataFile(String name, String folder) {
        this.name = name;
        this.folder = folder;

        if (folder == null)
            file = Paths.get(ParekClient.getInstance().dir.toString(), name);
        else
            file = Paths.get(ParekClient.getInstance().dir.toString(), folder, name);
    }

    public void write() throws IOException {
        if (Files.exists(file) && Files.isDirectory(file)) {
            FileUtils.deleteDirectory(file.toFile());
        }

        if (!Files.exists(file))
            Files.createFile(file);

        try (final FileWriter fileWriter = new FileWriter(file.toFile())) {
            fileWriter.write(JSON.toJSONString(getData()));
        }
    }

    public void read() {
        if (!Files.exists(file))
            return;

        final StringBuilder buffer = new StringBuilder();

        try (final Scanner scanner = new Scanner(file.toFile())) {
            while (scanner.hasNext())
                buffer.append(scanner.nextLine());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        final String contents = buffer.toString();

        final JSONObject jsonObject = JSON.parseObject(contents);

        setData(jsonObject);
    }

    public abstract JSONObject getData();

    public abstract void setData(JSONObject object);
}
