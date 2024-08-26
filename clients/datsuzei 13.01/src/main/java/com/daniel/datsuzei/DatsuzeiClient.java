package com.daniel.datsuzei;

import com.daniel.datsuzei.command.CommandManager;
import com.daniel.datsuzei.command.CommandTemplate;
import com.daniel.datsuzei.event.Event;
import com.daniel.datsuzei.file.FileManager;
import com.daniel.datsuzei.font.FontManager;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.settings.SettingManager;
import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import com.daniel.datsuzei.util.logger.NamedLogger;
import com.daniel.datsuzei.util.player.PlayerUtil;
import com.github.jezevcik.eventbus.bus.Bus;
import com.github.jezevcik.eventbus.bus.impl.EventBus;
import lombok.Getter;
import org.lwjglx.opengl.Display;
import tv.twitch.broadcast.EncodingCpuUsage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
public final class DatsuzeiClient implements MinecraftClient {

    public static final String NAME = "Datsuzei";
    public static final String CJ_NAME = "脱税";
    public static final String VERSION = "1.0.0";
    public static final String CHAT_PREFIX = STR."(\{NAME}): ";
    public static final char COMMAND_EXECUTE_PREFIX = '.';

    private static volatile DatsuzeiClient datsuzeiClient;

    private final Bus<Event> eventBus = new EventBus<>();
    private final NamedLogger logger = new NamedLogger(NAME);
    private final File directory = new File(mc.mcDataDir, NAME);
    private final ExecutorService threadpool = Executors.newCachedThreadPool();

    // Launching client
    private boolean finishedLaunch;
    private Future<Integer> clientLaunchTask;
    private long lastPrintOut;

    // Generic managers
    // None atm.

    public void onPreMinecraftStart() {
        // The pre minecraft launch doesn't depend on Minecraft's components, therefore it can run asynchronously
        clientLaunchTask = threadpool.submit(() -> {
            // Log the fact that the client's start method has been initialized
            logger.info("Starting Datsuzei Client's async launch");

            // Create the client's directory
            if(!directory.exists())
                if(!directory.mkdirs())
                    // Return error 1 if the client's directory creating failed
                    return 1;

            // Initialize managers
            try {
                FontManager.getSingleton().preMinecraftLaunch();
                ModuleManager.getSingleton().preMinecraftLaunch();
                SettingManager.getSingleton().preMinecraftLaunch();
                CommandManager.getSingleton().preMinecraftLaunch();
                FileManager.getSingleton().preMinecraftLaunch();
            } catch (Exception e) {
                // Report error and return if the initialization has failed
                logger.error("Failed to initialize managers in the async launch:", e);
                return 2;
            }

            // Anything other than 0 is an error
            return 0;
        });
    }

    public void onPostMinecraftStart() {
        try {
            // Make sure that the async launch is finished before continuing
            while (!clientLaunchTask.isDone()) {
                if(System.currentTimeMillis() - lastPrintOut >= 750 || lastPrintOut == 0) {
                    logger.info("Waiting for client launch task to finish...");
                    lastPrintOut = System.currentTimeMillis();
                }
            }
            // Check if the async launch has encountered an error, and if so crash
            int errorCode = clientLaunchTask.get();
            if(errorCode != 0) {
                throw new RuntimeException(STR."The client encountered an error in the async launch method. Error code is \{clientLaunchTask.get()}");
            } else {
                logger.info("Async launch finished successfully");
            }

            // Log the fact that the post-minecraft initializations have started
            logger.info("Starting Datsuzei Client's post-minecraft launch");

            // Fetch latest contributors from GitHub and set the window title, put in post minecraft to make sure it doesn't get overwritten
            Display.setTitle(STR."\{NAME} v\{VERSION} \{errorCode == 0 ? "" : STR." - Loaded with error code \{errorCode}"}");

            // Initialize managers
            try {
                FontManager.getSingleton().postMinecraftLaunch();
                ModuleManager.getSingleton().postMinecraftLaunch();
                SettingManager.getSingleton().postMinecraftLaunch();
                CommandManager.getSingleton().postMinecraftLaunch();
                FileManager.getSingleton().postMinecraftLaunch();
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                     IllegalAccessException e) {
                logger.error("Failed to initialize managers on post Minecraft launch", e);
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("The post minecraft launch method has encountered an error, while trying to interact with the async launch:", e);
        }

        finishedLaunch = true;
    }

    public void onStop() {
        logger.info("Stopping Datsuzei Client");

        // This is for if the user managed to crash or stop the client before the file manager loaded, resetting their settings
        if(finishedLaunch)
            FileManager.getSingleton().save();
    }

    public static DatsuzeiClient getSingleton() {
        if(datsuzeiClient == null)
            datsuzeiClient = new DatsuzeiClient();

        return datsuzeiClient;
    }

    public boolean isDeveloper() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

}
