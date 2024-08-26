package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;

public class Main {
    public static void main(String[] programArguments) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        final String legacyLauncherProperty = System.getProperty("legacy-launcher");
        final boolean legacyLauncher = Boolean.parseBoolean(legacyLauncherProperty);

        final OptionParser minecraftOptionParser = new OptionParser();

        minecraftOptionParser.allowsUnrecognizedOptions();
        minecraftOptionParser.accepts(!legacyLauncher ? "full-screen" : "fullscreen");
        minecraftOptionParser.accepts(!legacyLauncher ? "check-gl-errors" : "checkGlErrors");
        
        final OptionSpec<String> serverOption = minecraftOptionParser.accepts("server")
                .withRequiredArg();
        final OptionSpec<Integer> portOption = minecraftOptionParser.accepts("port")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(25565);
        final OptionSpec<File> gameDirOption = minecraftOptionParser.accepts(!legacyLauncher ? "game" : "gameDir")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("."));
        final OptionSpec<File> assetsDirOption = minecraftOptionParser.accepts(!legacyLauncher ? "assets" : "assetsDir")
                .withRequiredArg()
                .ofType(File.class);
        final OptionSpec<File> resourcePackDirOption = minecraftOptionParser.accepts(!legacyLauncher ? "resource-packs" : "resourcePackDir")
                .withRequiredArg()
                .ofType(File.class);
        final OptionSpec<String> proxyHostOption = minecraftOptionParser.accepts(!legacyLauncher ? "proxy-host" : "proxyHost")
                .withRequiredArg();
        final OptionSpec<Integer> proxyPortOption = minecraftOptionParser.accepts(!legacyLauncher ? "proxy-port" : "proxyPort")
                .withRequiredArg()
                .defaultsTo("8080")
                .ofType(Integer.class);
        final OptionSpec<String> proxyUserOption = minecraftOptionParser.accepts(!legacyLauncher ? "proxy-user" : "proxyUser")
                .withRequiredArg();
        final OptionSpec<String> proxyPassOption = minecraftOptionParser.accepts(!legacyLauncher ? "proxy-pass" : "proxyPass")
                .withRequiredArg();
        final OptionSpec<String> usernameOption = minecraftOptionParser.accepts("username")
                .withRequiredArg()
                .defaultsTo(STR."AtlasUser\{(int) (Math.random() * 100)}");
        final OptionSpec<String> uuidOption = minecraftOptionParser.accepts("uuid")
                .withRequiredArg();
        final OptionSpec<String> accessTokenOption = minecraftOptionParser.accepts(!legacyLauncher ? "token" : "accessToken")
                .withRequiredArg()
                .required();
        final OptionSpec<String> versionOption = minecraftOptionParser.accepts("version")
                .withRequiredArg()
                .required();
        final OptionSpec<Integer> widthOption = minecraftOptionParser.accepts("width")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(854);
        final OptionSpec<Integer> heightOption = minecraftOptionParser.accepts("height")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(480);
        final OptionSpec<String> userPropertiesOption = minecraftOptionParser.accepts(!legacyLauncher ? "user" : "userProperties")
                .withRequiredArg()
                .defaultsTo("{}");
        final OptionSpec<String> profilePropertiesOption = minecraftOptionParser.accepts(!legacyLauncher ? "profile" : "profileProperties")
                .withRequiredArg()
                .defaultsTo("{}");
        final OptionSpec<String> assetIndexOption = minecraftOptionParser.accepts(!legacyLauncher ? "asset-index" : "assetIndex")
                .withRequiredArg();
        final OptionSpec<String> userTypeOption = minecraftOptionParser.accepts(!legacyLauncher ? "session-type" : "userType")
                .withRequiredArg()
                .defaultsTo("legacy");
        final OptionSpec<String> nonOptionsOption = minecraftOptionParser.nonOptions();

        final OptionSet optionSet = minecraftOptionParser.parse(programArguments);
        List<String> ignoredArguments = optionSet.valuesOf(nonOptionsOption);

        if (!ignoredArguments.isEmpty()) {
            System.out.println(STR."Completely ignored arguments: \{ignoredArguments}");

            for (String arg : ignoredArguments) {
                if (arg.toLowerCase().startsWith("-x") || arg.toLowerCase().startsWith("-agent"))
                    System.out.println(STR."Warning: \{arg} looks like a JVM argument. Are you sure it should be a program argument?");
            }
        }

        String proxyHost = optionSet.valueOf(proxyHostOption);
        Proxy proxy = Proxy.NO_PROXY;

        if (proxyHost != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, optionSet.valueOf(proxyPortOption)));
            } catch (Exception ignored) {
            }
        }

        final String proxyUser = optionSet.valueOf(proxyUserOption);
        final String proxyPass = optionSet.valueOf(proxyPassOption);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPass)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                }
            });
        }

        int width = optionSet.valueOf(widthOption);
        int height = optionSet.valueOf(heightOption);
        boolean fullscreen = optionSet.has(!legacyLauncher ? "full-screen" : "fullscreen");
        boolean checkGlErrors = optionSet.has(!legacyLauncher ? "check-gl-errors" : "checkGlErrors");
        String version = optionSet.valueOf(versionOption);

        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap userProperties = gson.fromJson(optionSet.valueOf(userPropertiesOption), PropertyMap.class);
        PropertyMap profileProperties = gson.fromJson(optionSet.valueOf(profilePropertiesOption), PropertyMap.class);

        File gameDir = optionSet.valueOf(gameDirOption);
        File assetsDir = optionSet.has(assetsDirOption) ? optionSet.valueOf(assetsDirOption) : new File(gameDir, "assets/");
        File resourcePackDir = optionSet.has(resourcePackDirOption) ? optionSet.valueOf(resourcePackDirOption) : new File(gameDir, "resourcepacks/");

        String uuid = optionSet.has(uuidOption) ? optionSet.valueOf(uuidOption) : optionSet.valueOf(usernameOption);
        String assetIndex = optionSet.has(assetIndexOption) ? optionSet.valueOf(assetIndexOption) : null;
        String server = optionSet.valueOf(serverOption);
        Integer port = optionSet.valueOf(portOption);
        String accessToken = optionSet.valueOf(accessTokenOption);
        String userType = optionSet.valueOf(userTypeOption);

        Session session = new Session(optionSet.valueOf(usernameOption), uuid, accessToken, userType);
        GameConfiguration gameConfiguration = new GameConfiguration(
                new GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy),
                new GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors),
                new GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex),
                new GameConfiguration.GameInformation(version),
                new GameConfiguration.ServerInformation(server, port));

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });

        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameConfiguration)).run();

    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
