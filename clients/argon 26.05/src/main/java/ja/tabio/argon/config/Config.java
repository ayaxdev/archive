package ja.tabio.argon.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.utils.string.EncryptionUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

public abstract class Config {

    public final String name;
    public final boolean encrypted;

    public final File file;

    public Config(String name, boolean encrypted) {
        this.name = name;
        this.encrypted = encrypted;

        this.file = new File(Argon.getInstance().directory, name);
    }

    protected abstract JsonObject get();

    protected abstract void set(JsonObject jsonObject);

    public final void save() {
        // Delete the file's old version
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to delete old file", e);
                return;
            }
        }

        final Gson gson = Argon.getInstance().configManager.gson;

        final String json = gson.toJson(get());
        final StringBuilder output = new StringBuilder();

        if (encrypted) {
            try {
                // Generate random key
                final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);

                final SecretKey secretKey = keyGen.generateKey();

                // Encode random key to string
                final String asString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

                // Insert a separator between the key and the encrypted data
                // This is a random string, but some letters have been replaced with cyrillic to avoid possible bug where this string already exists
                output.append("#");
                output.append(asString);
                output.append("vtLhХWUоR");

                // Append encrypted data
                output.append(EncryptionUtils.encrypt("AES/CBC/PKCS5Padding", json, secretKey, new IvParameterSpec(
                        new byte[] {24, 34, 82, 104, 20, 29, 21, 59, 73, 29, 19, 48, 22, 31, 45, 37}
                )));
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to encrypt file", e);
                return;
            }
        } else {
            output.append(json);
        }

        try {
            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(output.toString());
            }
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to write to file", e);
        }
    }

    public final void read() {
        if (!file.exists())
            return;

        final Gson gson = Argon.getInstance().configManager.gson;

        final StringBuilder jsonBuilder = new StringBuilder();

        try {
            try(Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
                while (scanner.hasNext())
                    jsonBuilder.append(scanner.next());
            }
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to read file", e);
        }

        final String read = jsonBuilder.toString();

        String output;
        if (read.startsWith("#")) {
            try {
                // Split contents of the file based on the separator
                final String[] split = read.split("vtLhХWUоR");
                final String key = split[0].substring(1);
                final String contents = split[1];

                // Decode the base64 encoded string
                final byte[] decodedKey = Base64.getDecoder().decode(key);
                // Rebuild key using SecretKeySpec
                final SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

                // Get decrypted contents
                output = EncryptionUtils.decrypt("AES/CBC/PKCS5Padding", contents, originalKey, new IvParameterSpec(
                        new byte[] {24, 34, 82, 104, 20, 29, 21, 59, 73, 29, 19, 48, 22, 31, 45, 37}
                ));
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to decrypt file", e);
                return;
            }
        } else {
            output = read;
        }

        final JsonObject jsonObject = gson.fromJson(output, JsonObject.class);

        set(jsonObject);
    }

}
