package com.atani.nextgen;

import com.atani.nextgen.event.impl.BackgroundEvent;
import com.atani.nextgen.font.FontManager;
import com.atani.nextgen.keybind.KeybindingManager;
import com.atani.nextgen.module.ModuleManager;
import com.atani.nextgen.processor.ProcessorManager;
import com.atani.nextgen.setting.SettingManager;
import com.atani.nextgen.util.logger.NamedLogger;
import com.atani.nextgen.util.minecraft.MinecraftClient;
import de.florianmichael.viamcp.ViaMCP;
import io.github.racoondog.norbit.EventBus;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.*;

public final class AtaniClient implements MinecraftClient {

    // Constants
    public static final String[] DEVELOPERS = new String[] {"Liticane", "jezevcik", "Geuxy"};
    public static final String NAME = "Atani",
            VERSION = "0.1.0",
            DEVELOPERS_JOINED = String.join(", ", DEVELOPERS);


    // Non-static variables
    public final File directory = new File(mc.mcDataDir, NAME.toLowerCase());
    public final NamedLogger logger = new NamedLogger(NAME);
    public final EventBus eventPubSub = EventBus.threadSafe();
    public final ExecutorService threadpool = Executors.newCachedThreadPool();

    // Implementation of the singleton design pattern
    private static volatile AtaniClient singleton;

    // Launching client
    private Future<Boolean> clientLaunchTask;
    public boolean launched = false;

    // This method is executed before Minecraft and serves to asynchronously initialize components of the client that do not require Minecraft's resources
    public void preMinecraftLaunch() {
        // Notifying about launching of the pre mc launch
        logger.info("Starting the pre Minecraft launch");

        clientLaunchTask = threadpool.submit(() -> {
            // Setting up event bus
            eventPubSub.registerLambdaFactory(getClass().getPackage().getName(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

            // Creating the directory
            if(!directory.exists())
                if(!directory.mkdirs()) {
                    logger.error("Failed to create the client's directory!");
                    return false;
                }

            // Setting the title
            Display.setTitle(STR."\{NAME} \{VERSION} by \{DEVELOPERS_JOINED}");

            // Creating the thread housing the BackgroundEvent call
            CompletableFuture.runAsync(() -> {
               for(;;) {
                   AtaniClient.getInstance().eventPubSub.publish(new BackgroundEvent());
               }
            });

            // Initializing managers
            FontManager.getSingleton().preMinecraftLaunch();
            ProcessorManager.getSingleton().preMinecraftLaunch();
            ModuleManager.getSingleton().preMinecraftLaunch();
            KeybindingManager.getSingleton().preMinecraftLaunch();
            SettingManager.getSingleton().preMinecraftLaunch();

            // Notifying about finishing of the pre mc launch
            logger.info("Stopping the pre Minecraft launch");

            return true;
        });
    }

    // This method is executed when Minecraft is initialized so anything that uses anything from Minecraft should be put here
    public void postMinecraftLaunch() {
        try {
            // Notifying about launching of the post mc launch
            logger.info("Starting the post Minecraft launch");

            // Setting the start of loading
            final long loadStart = System.currentTimeMillis();

            // Make sure that the async launch is finished before continuing
            while (!clientLaunchTask.isDone()) {
                System.out.print(STR."Loading for \{(System.currentTimeMillis() - loadStart) / 1000} seconds\r");
            }

            // Check if the async launch has encountered an error, and if so stop loading the client
            if(!clientLaunchTask.get()) {
                throw new RuntimeException("Client Launch Task failed");
            }

            // Creating ViaMCP
            ViaMCP.create();

            // Initializing managers
            FontManager.getSingleton().postMinecraftLaunch();
            ProcessorManager.getSingleton().postMinecraftLaunch();
            ModuleManager.getSingleton().postMinecraftLaunch();
            KeybindingManager.getSingleton().postMinecraftLaunch();
            SettingManager.getSingleton().postMinecraftLaunch();

            // Notifying about finishing of the post mc launch
            logger.info("Stopping the post Minecraft launch");

            // Marking the client as successfully launched
            launched = true;
        } catch (ExecutionException | InterruptedException exception) {
            logger.error("The post minecraft launch method has encountered an error while trying to interact with the async launch:", exception);
        } catch (Exception exception) {
            logger.error("The post minecraft launch method has encountered an unknown error:", exception);
        }
    }

    public void stopClient() {

    }

    public static AtaniClient getInstance() {
        return singleton == null ? singleton = new AtaniClient() : singleton;
    }

}
