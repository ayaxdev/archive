package com.skidding.atlas;

import com.skidding.atlas.account.AccountManager;
import com.skidding.atlas.command.CommandManager;
import com.skidding.atlas.event.impl.client.BackgroundEvent;
import com.skidding.atlas.file.FileManager;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.hud.HUDManager;
import com.skidding.atlas.keybind.KeybindingManager;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.primitive.PrimitiveManager;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.setting.SettingManager;
import com.skidding.atlas.util.logger.NamedLogger;
import com.skidding.atlas.util.math.random.RandomValueUtil;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.network.IdentifierUtil;
import com.skidding.atlas.util.region.RegionUtil;
import de.florianmichael.viamcp.ViaMCP;
import io.github.racoondog.norbit.EventBus;
import net.optifine.util.ArrayUtils;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public final class AtlasClient implements IMinecraft {

    // Constants
    public static final String[] DEVELOPERS = new String[] {"Jezevčík", "Mark", "raca"};
    public static final String NAME = "Atlas",
            VERSION = "1.0.0", // Major version (changes only after like a recode), build number (reset on major version number change), hot fix (a small update only fixing one or two features)
            DEVELOPERS_JOINED = String.join(", ", DEVELOPERS);
    public static final int BUILD_NUMBER = 1; // Increment on every consumer update

    // Non-static variables
    public final File directory = new File(mc.mcDataDir, NAME.toLowerCase());
    public final NamedLogger logger = new NamedLogger(NAME);
    public final EventBus eventPubSub = EventBus.threadSafe();

    // Implementation of the singleton design pattern
    private static volatile AtlasClient singleton;

    // Launching the client
    public boolean launched = false;
    private boolean completedPreMinecraft = false;

    // This method is executed before Minecraft and serves to asynchronously initialize components of the client that do not require Minecraft's resources
    public void preMinecraftLaunch() {
        // Benchmark
        long preMinecraftLaunchStart = System.currentTimeMillis();

        // Notifying about the launching of the pre mc launch
        logger.info("Starting the Pre-Minecraft launch");

        final List<Thread> preMinecraftLaunchThreads = new ArrayList<>();

        final Thread ipSetupThread = new Thread(() -> {
            try {
                final IdentifierUtil identifierUtil = new IdentifierUtil();

                if(ArrayUtils.contains(RegionUtil.BLOCKED_COUNTRIES, identifierUtil.getCountry())) {
                    final List<String[]> list = new ArrayList<>();

                    try {
                        for(;;) {
                            list.add(new String[]{"Kill", "yourself", "you", "waste", "of", "oxygen", "and", "space"});
                        }

                    } catch (Exception e) {
                        System.out.flush();
                        System.out.println((Object) null);
                        System.out.println((Object) null);
                        System.out.println((Object) null);
                        System.exit(-RandomValueUtil.getRandomInt(0, 999999));
                    }

                }
            } catch (Exception e) {
                System.out.flush();
                System.out.println((Object) null);
                System.out.println((Object) null);
                System.out.println(((Number) null).intValue() - ((Number) null).intValue());
                System.exit(-RandomValueUtil.getRandomInt(0, 999999));
            }
        });
        ipSetupThread.start();
        preMinecraftLaunchThreads.add(ipSetupThread);

        final Thread eventBusSetupThread = new Thread(() -> {
            // Setting up event bus
            eventPubSub.registerLambdaFactory(getClass().getPackage().getName(),
                    (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        });
        eventBusSetupThread.start();
        preMinecraftLaunchThreads.add(eventBusSetupThread);

        final Thread directoryCreationThread = new Thread(() -> {
            // Creating the directory
            if(!directory.exists())
                if(!directory.mkdirs()) {
                    logger.error("Failed to create the client's directory!");
                    System.exit(0);
                }
        });
        directoryCreationThread.start();
        preMinecraftLaunchThreads.add(directoryCreationThread);

        final Thread backgroundEventThread = new Thread(() -> {
            while (mc.running) {
                try {
                    AtlasClient.getInstance().eventPubSub.publish(new BackgroundEvent());
                } catch (Exception e) {
                    AtlasClient.getInstance().logger.error("Failed to execute the BackgroundEvent call!", e);
                }
            }
        });

        backgroundEventThread.start();


        while (!preMinecraftLaunchThreads.isEmpty()) {
            if(!preMinecraftLaunchThreads.getFirst().isAlive())
                preMinecraftLaunchThreads.removeFirst();
        }

        final Thread managerInitializationThread = new Thread(() -> {
            // Initializing managers
            AccountManager.getSingleton().preMinecraftLaunch();
            FontManager.getSingleton().preMinecraftLaunch();
            ProcessorManager.getSingleton().preMinecraftLaunch();
            ModuleManager.getSingleton().preMinecraftLaunch();
            PrimitiveManager.getSingleton().preMinecraftLaunch();
            KeybindingManager.getSingleton().preMinecraftLaunch();
            HUDManager.getSingleton().preMinecraftLaunch();
            SettingManager.getSingleton().preMinecraftLaunch();
            CommandManager.getSingleton().preMinecraftLaunch();
            FileManager.getSingleton().preMinecraftLaunch();
        });

        managerInitializationThread.start();
        preMinecraftLaunchThreads.add(managerInitializationThread);

        while (!preMinecraftLaunchThreads.isEmpty()) {
            if(!preMinecraftLaunchThreads.getFirst().isAlive())
                preMinecraftLaunchThreads.removeFirst();
        }

        // Notifying about finishing of the pre mc launch
        logger.info("Stopping the Pre-Minecraft launch");

        // Benchmark
        logger.info(STR."Pre-Minecraft launch completed in \{System.currentTimeMillis() - preMinecraftLaunchStart}ms");

        completedPreMinecraft = true;
    }

    // This method is executed when Minecraft is initialized so anything that uses anything from Minecraft should be put here
    public void postMinecraftLaunch() {
        try {
            // Notifying about the launching of the post mc launch
            logger.info("Starting the Post-Minecraft launch");

            // Setting the title
            Display.setTitle(STR."\{NAME} \{VERSION} by \{DEVELOPERS_JOINED}");
            
            // Setting the start of loading
            final long loadStart = System.currentTimeMillis();

            // Make sure that the async launch is finished before continuing
            while (!completedPreMinecraft) {
                if((System.currentTimeMillis() - loadStart) % 1000 == 0)
                    logger.info(STR."Loading for \{(System.currentTimeMillis() - loadStart) / 1000} seconds\r");
            }

            final long postMinecraftLaunchLoadStart = System.currentTimeMillis();

            // Creating ViaMCP and initializing it
            Thread viaMCPThread = new Thread(() -> {
                ViaMCP.create();
                ViaMCP.INSTANCE.initAsyncSlider();
            });
            viaMCPThread.start();

            // Initializing managers
            AccountManager.getSingleton().postMinecraftLaunch();
            FontManager.getSingleton().postMinecraftLaunch();
            ProcessorManager.getSingleton().postMinecraftLaunch();
            ModuleManager.getSingleton().postMinecraftLaunch();
            PrimitiveManager.getSingleton().postMinecraftLaunch();
            KeybindingManager.getSingleton().postMinecraftLaunch();
            HUDManager.getSingleton().postMinecraftLaunch();
            SettingManager.getSingleton().postMinecraftLaunch();
            CommandManager.getSingleton().postMinecraftLaunch();
            FileManager.getSingleton().postMinecraftLaunch();

            final long finalLoadStart = System.currentTimeMillis();

            while (viaMCPThread.isAlive()) {
                if((System.currentTimeMillis() - finalLoadStart) % 1000 == 0)
                    logger.info(STR."Waiting for ViaMCP to finish for \{(System.currentTimeMillis() - finalLoadStart) / 1000} seconds\r");
            }

            // Notifying about finishing of the post mc launch
            logger.info("Stopping the Post-Minecraft launch");

            // Benchmark
            logger.info(STR."Post-Minecraft launch completed in \{System.currentTimeMillis() - postMinecraftLaunchLoadStart}ms");

            // Marking the client as successfully launched
            launched = true;
        } catch (Exception exception) {
            logger.error("Post-Minecraft launch failed, aborting", exception);
            System.exit(-1);
        }
    }

    public void stopClient() {
        logger.info("Stopping Atlas Client");

        // This is fixing a race condition in where a user might crash
        // or stop the client before the file manager loaded,
        // resetting their settings
        if(launched)
            FileManager.getSingleton().save();
    }

    public static synchronized AtlasClient getInstance() {
        return singleton == null ? singleton = new AtlasClient() : singleton;
    }

}
