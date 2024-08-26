package com.skidding.atlas.util.minecraft.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionUtil {

    public static final SessionUtil INSTANCE = new SessionUtil();

    public List<LoadedAccount> loadAccounts(File selectedFile) {
        List<LoadedAccount> loadedAccounts = new ArrayList<>();

        if (selectedFile == null) {
            return loadedAccounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    loadedAccounts.add(new LoadedAccount(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return loadedAccounts;
    }

    public static class LoadedAccount {
        public String username, password;

        public LoadedAccount(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

}
