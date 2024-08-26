package lord.daniel.alexander;

import com.google.common.base.Charsets;
import io.github.nevalackin.homoBus.bus.Bus;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import lombok.Getter;
import lord.daniel.alexander.alexandergui.AlexanderGuiScreen;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiCrackedLogin;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiMicrosoftOpenAuthLogin;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiMicrosoftWebLogin;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.file.storage.FileStorage;
import lord.daniel.alexander.handler.game.SessionHandler;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.handler.plaxer.TargetHandler;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.storage.impl.*;
import lord.daniel.alexander.util.java.ArrayUtils;
import lord.daniel.alexander.util.java.StringUtil;
import lord.daniel.alexander.util.math.time.TimeUnitUtil;
import lord.daniel.alexander.util.network.IPUtil;
import lord.daniel.alexander.util.network.InternetUtil;
import lord.daniel.alexander.util.region.RegionUtil;
import lord.daniel.alexander.util.weather.WeatherUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public enum Modification implements Methods {
    INSTANCE;

    public static final String NAME = "Alexander", VERSION = "0.0.1", DEVELOPMENT_VERSION = "1", BUILD_NUMBER = "1", PREFIX = "§6§o" + NAME + " §r§f» ", COMMAND_PREFIX = ".";
    public static final String ASSETS_DIR = "alexander", FULL_ASSETS_DIR = "assets/minecraft/alexander",
            FONTS_DIR = ASSETS_DIR + "/fonts/", FULL_FONTS_DIR = FULL_ASSETS_DIR + "/fonts/",
            SHADERS_DIR = ASSETS_DIR + "/shaders/", FULL_SHADERS_DIR = FULL_ASSETS_DIR + "/shaders/",
            BACKGROUNDS_DIR = ASSETS_DIR + "/shaders/fragment/backgrounds", FULL_BACKGROUNDS_DIR = FULL_ASSETS_DIR + "/shaders/fragment/backgrounds",
            TEXTURES_DIR = ASSETS_DIR + "/textures/", FULL_TEXTURES_DIR = FULL_ASSETS_DIR + "/textures/",
            TEXTS_DIR = ASSETS_DIR + "/texts/", FULL_TEXTS_DIR = FULL_ASSETS_DIR + "/texts/";
    public static final String[] CHANGELOG = new String[] {"+ Made the client"};

    private File rootDir, fileDir, configDir;

    private final Bus<Event> bus = new EventBus<>();
    private boolean nonWhite = false;
    private final ArrayList<Object> memoryWaster = new ArrayList<>();

    private String response, ip, country;
    private double latitude, longitude;

    private WeatherUtil.ParsedWeather currentWeather;

    private long initTime = 0L;
    private long settingsLoaded, modulesLoaded;

    private String lastRpcState = null;

    private Thread continuousThread;

    private boolean offlineMode = false; //TODO: Once protection is added make it so that only devs can use this

    public void startClient() {
        try {
            offlineMode = !InternetUtil.isInternetAvailable();
        } catch (IOException e) {
            e.printStackTrace();
            offlineMode = true;
        }

        SecureRandom secureRandom = null;

        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");;
        } catch (NoSuchAlgorithmException e) {
            genericExit();
        }

        assert secureRandom != null;

        String message;

        try {
            final ResourceLocation location = new ResourceLocation(TEXTS_DIR + "splashes.txt");
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 24)
                message = "Merry X-mas!";
            else if (calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1)
                message = "Happy new year!";
            else if (calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) == 31)
                message = "OOoooOOOoooo! Spooky!";
            else if (calendar.get(Calendar.MONTH) + 1 == 11 && calendar.get(Calendar.DATE) == 17)
                message = "Svoboda musí být vybojována krví!";
            else if (calendar.get(Calendar.MONTH) + 1 == 9 && calendar.get(Calendar.DATE) == 28)
                message = "Sláva!";
            else {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream(), Charsets.UTF_8));
                final List<String> list = reader.lines().toList();
                reader.close();
                if (!list.isEmpty())
                    message = list.get(Math.abs(secureRandom.nextInt(list.size())));
                else
                    message = "Sad";
            }
        } catch (IOException e) {
            message = "No message for you!";
        }

        assert message != null;

        Display.setTitle(Modification.NAME + " v" + Modification.VERSION + " (Version " + Modification.DEVELOPMENT_VERSION + ")" + ": " + message);

        if(!offlineMode) {
            response = IPUtil.getIPInfo();

            this.ip = IPUtil.getIp(response);
            this.country = IPUtil.getCountry(response);

            String[] location = IPUtil.getLoc(response).split(",");

            this.latitude = Double.parseDouble(location[0]);
            this.longitude = Double.parseDouble(location[1]);
        } else {
            this.ip = "127.0.0.1";
            this.country = System.getProperty("user.county");
            this.latitude = -1;
            this.longitude = -1;
        }

        if(ArrayUtils.contains(RegionUtil.AFRICAN_COUNTRIES, country) || ArrayUtils.contains(RegionUtil.KIKED_COUNTRIES, country) || ArrayUtils.contains(RegionUtil.WESTERN_ASIA_COUNTRIES, country) || ArrayUtils.contains(RegionUtil.SOUTH_ASIAN_COUNTRIES, country))
            nonWhite = true;

        setupRoot("KawaiiLoliSexClient");

        FontStorage fontStorage = new FontStorage();
        BackgroundStorage backgroundStorage = new BackgroundStorage();
        ModuleStorage moduleStorage = new ModuleStorage();
        CommandStorage commandStorage = new CommandStorage();
        DraggableStorage draggableStorage = new DraggableStorage();
        AltStorage altStorage = new AltStorage();
        FileStorage fileStorage = new FileStorage();
        ConfigStorage configStorage = new ConfigStorage();

        FontStorage.setFontStorage(fontStorage);
        BackgroundStorage.setBackgroundStorage(backgroundStorage);
        ModuleStorage.setModuleStorage(moduleStorage);
        CommandStorage.setCommandStorage(commandStorage);
        DraggableStorage.setDraggableStorage(draggableStorage);
        AltStorage.setAltStorage(altStorage);
        FileStorage.setFileStorage(fileStorage);
        ConfigStorage.setConfigStorage(configStorage);

        FontStorage.getFontStorage().init();
        BackgroundStorage.getBackgroundStorage().init();
        ModuleStorage.getModuleStorage().init();
        CommandStorage.getCommandStorage().init();
        DraggableStorage.getDraggableStorage().init();
        AltStorage.getAltStorage().init();
        FileStorage.getFileStorage().init();
        ConfigStorage.getConfigStorage().init();

        getBus().subscribe(new PlayerHandler());
        getBus().subscribe(new SessionHandler());
        getBus().subscribe(new TargetHandler());

        AtomicInteger settingsCounter = new AtomicInteger(0);

        ModuleStorage.getModuleStorage().getList().forEach(abstractModule -> {
            settingsCounter.set(settingsCounter.get() + abstractModule.getSettings().size());
        });

        System.out.println("Loaded " + (modulesLoaded = ModuleStorage.getModuleStorage().getList().size()) + " Modules");
        System.out.println("Loaded " + (settingsLoaded = settingsCounter.get())  + " Settings");

        DiscordRPC.discordRunCallbacks();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("m");
        final int rpcLogo = (int) Math.min(4, secureRandom.nextDouble() * 5);

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            if (user.userId.equalsIgnoreCase("697689758599741461")) {
                System.out.printf("Hey, mommy! I just wanted to say that i luv you. ~ heritsy");
            }
            System.out.println("Welcome " + user.username);
        }).build();

        DiscordRPC.discordInitialize("1155020537794408478", handlers, true);

        final DiscordRichPresence rich = new DiscordRichPresence.Builder("Logging in...")
                .setBigImage("logo" + rpcLogo, String.format("%s %s", NAME, VERSION))
                .build();

        DiscordRPC.discordUpdatePresence(rich);

        this.continuousThread = new Thread(() -> {
            while (true) {
                try {
                    long runningTime = (System.currentTimeMillis() - initTime);

                    if(!offlineMode) {
                        if(currentWeather == null || runningTime % 6000 == 0) {
                            try {
                                currentWeather = WeatherUtil.getWeather();
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Failed to update weather!");
                            }
                        }

                        if(runningTime % 1000 == 0) {
                            String state = null;

                            if(mc.thePlayer != null && mc.theWorld != null) {
                                state = mc.isSingleplayer() || mc.getCurrentServerData() == null ? "Playing SinglePlayer" : "Playing on " + mc.getCurrentServerData().serverIP;
                            } else if(mc.currentScreen instanceof AlexanderGuiScreen || mc.currentScreen instanceof GuiCrackedLogin || mc.currentScreen instanceof GuiMicrosoftOpenAuthLogin || mc.currentScreen instanceof GuiMicrosoftWebLogin) {
                                state = "In Alexander Menu";
                            } else if(mc.currentScreen instanceof GuiMainMenu) {
                                state = "In the Main Menu";
                            } else if(mc.currentScreen instanceof GuiSelectWorld || mc.currentScreen instanceof GuiCreateWorld || mc.currentScreen instanceof GuiRenameWorld || mc.currentScreen instanceof GuiCustomizeWorldScreen || mc.currentScreen instanceof GuiCreateFlatWorld) {
                                state = "Selecting a world";
                            } else if(mc.currentScreen instanceof GuiMultiplayer || mc.currentScreen instanceof GuiScreenServerList || mc.currentScreen instanceof GuiScreenAddServer) {
                                state = "Selecting a server";
                            }

                            if((state != null && (lastRpcState == null || !lastRpcState.equals(state))) || runningTime % 60000 == 0) {
                                if(state == null)
                                    state = lastRpcState;

                                String minutes = simpleDateFormat.format(runningTime);

                                DiscordRichPresence presence = new DiscordRichPresence.Builder(state)
                                        .setBigImage("logo" + rpcLogo, String.format("%s %s", NAME, VERSION))
                                        .setDetails("Playing for " + minutes + (Integer.parseInt(minutes) == 1 ? " minute" : " minutes"))
                                        .build();

                                DiscordRPC.discordUpdatePresence(presence);
                                lastRpcState = state;
                            }
                        }

                        DiscordRPC.discordRunCallbacks();
                    }

                    if(isNonWhite()) {
                        if(runningTime % 10 == 0) {
                            memoryWaster.add(new Object());
                        }
                        if(runningTime >= TimeUnitUtil.getMsFromMinutes(10)) {
                            break;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    genericExit();
                }
            }
        });

        this.continuousThread.start();

        initTime = System.currentTimeMillis();
    }

    public void endClient() {
        DiscordRPC.discordShutdown();
        FileStorage.getFileStorage().save();
    }

    public void setupRoot(String name) {
        rootDir = new File(mc.mcDataDir, name);

        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new RuntimeException("Couldn't create rootDir " + rootDir.getAbsolutePath() + " :(");
            }
        }

        configDir = new File(rootDir, "configs");

        if (!configDir.exists()) {
            if (!configDir.mkdirs()) {
                throw new RuntimeException("Couldn't create configDir " + rootDir.getAbsolutePath() + " :(");
            }
        }

        fileDir = new File(rootDir, "files");

        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                throw new RuntimeException("Couldn't create fileDir " + rootDir.getAbsolutePath() + " :(");
            }
        }
    }

    public void genericExit() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            unsafe.putAddress(0, 0);
        } catch (Exception e) {
            System.exit(0);
        }
    }

}
