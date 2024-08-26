package ja.tabio.argon.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import ja.tabio.argon.Argon;
import ja.tabio.argon.config.annotation.ConfigData;
import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.interfaces.ISerializable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public abstract class Config implements INameable, ISerializable, Argon.IArgonAccess {

    public final String name;
    public final boolean encrypted;

    private final File file;

    public Config() {
        final ConfigData configData = getClass().getAnnotation(ConfigData.class);

        if (configData == null)
            throw new IllegalStateException(String.format("Config %s is not annotated with @ConfigData", getClass().getSimpleName()));

        this.name = configData.name();
        this.encrypted = configData.encrypted();

        this.file = new File(Argon.getInstance().directory, name + ".argon-config");
    }

    public void write() {
        try {
            final Path filePath = this.file.toPath();

            if (Files.exists(filePath))
                if (Files.isDirectory(filePath))
                    FileUtils.deleteDirectory(file);
                else
                    Files.delete(filePath);
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to delete old version of the {} config file", name, e);
            return;
        }

        try(final FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(serialize().toString(JSONWriter.Feature.PrettyFormat));
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to write to {} config's file", name, e);
        }
    }

    public void read() {
        final Path filePath = this.file.toPath();

        if (!Files.exists(filePath))
            return;
        else if (Files.isDirectory(filePath)) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to delete directory in place of the {} config file", name, e);
            }
            return;
        }

        try(final Scanner scanner = new Scanner(file)) {
            final StringBuilder stringBuilder = new StringBuilder();

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.next());
            }

            final String json = stringBuilder.toString();
            deserialize(JSON.parseObject(json));
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to read {} config's file", name, e);
        }
    }

    @Override
    public final String getName() {
        return name;
    }
}
