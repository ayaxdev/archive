package ja.tabio.argon;

import de.florianmichael.rclasses.common.FileUtils;
import imgui.ImGui;
import io.github.racoondog.norbit.EventBus;
import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.command.manager.CommandManager;
import ja.tabio.argon.component.imgui.ImGuiImpl;
import ja.tabio.argon.component.imgui.themes.ThemeTOMLParser;
import ja.tabio.argon.component.loader.Loader;
import ja.tabio.argon.component.loader.impl.AsyncLoader;
import ja.tabio.argon.component.loader.impl.SyncLoader;
import ja.tabio.argon.component.render.RenderManager;
import ja.tabio.argon.config.manager.ConfigManager;
import ja.tabio.argon.event.impl.GameTitleEvent;
import ja.tabio.argon.font.manager.FontManager;
import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.items.ItemGroupManager;
import ja.tabio.argon.module.manager.ModuleManager;
import ja.tabio.argon.processor.manager.ProcessorManager;
import ja.tabio.argon.setting.manager.SettingManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Argon implements ModInitializer, Initializable, Minecraft {

    /**
     * Implementation of the singleton design pattern
     */
    private static volatile Argon instance;

    public final static String MOD_ID = "argon";
    public final static ModMetadata METADATA = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();

    public final static String NAME = METADATA.getName();
    public final static String VERSION = METADATA.getVersion().getFriendlyString();
    public final static String AUTHORS = METADATA.getAuthors().stream().map(Person::getName).collect(Collectors.joining(", "));

    public final static String LATIN_NAME = NAME,
                        CYRILLIC_NAME = "Аргон";

    /**
     * Random value assigned on startup
     * Used for special events that are different on startup
     */
    public final double random = ThreadLocalRandom.current().nextDouble();

    /**
     * Client directory containing all the files
     */
    public final File directory = new File(System.getProperty("user.home"), ".argon");
    public final Logger logger = LoggerFactory.getLogger(CYRILLIC_NAME);
    public final EventBus eventBus = EventBus.threadSafe();

    public final RenderManager renderManager = new RenderManager();
    public final ProcessorManager processorManager = new ProcessorManager();
    public final FontManager fontManager = new FontManager();
    public final ModuleManager moduleManager = new ModuleManager();
    public final CommandManager commandManager = new CommandManager();
    public final SettingManager settingManager = new SettingManager();
    public final ItemGroupManager itemGroupManager = new ItemGroupManager();
    public final ConfigManager configManager = new ConfigManager();

    private Loader initLoader;

    public ImGuiImpl imGui;

    /**
     * Whether the client is fully initialized and started
     */
    public boolean loaded;

    @Override
    public void onInitialize() {
        // Don't use this
    }

    /**
     * Called after the client starts, but before Minecraft initialized
     *
     * @param args Launch arguments
     */
    @Override
    public void init(final String[] args) {
        if (args == null)
            throw new NullPointerException("Argon's launch arguments must no be null");

        // Useful for debugging
        if (ArrayUtils.contains(args, "-argonLoadSynchronously")) {
            logger.info("Using synchronous loader");
            initLoader = new SyncLoader();
        } else {
            logger.info("Using asynchronous loader");
            initLoader = new AsyncLoader();
        }

        initLoader.add(() -> {
            eventBus.registerLambdaFactory("ja.tabio.argon",
                    (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
            eventBus.subscribe(this);
        });

        initLoader.add(() -> {
            try {
                if (Files.exists(directory.toPath()) && !Files.isDirectory(directory.toPath()))
                    FileUtils.deleteFolder(directory);
            } catch (Exception e) {
                logger.error("Failed to delete file in place of client directory", e);
                System.exit(-1);
            }

            try {
                if (!Files.exists(directory.toPath()))
                    Files.createDirectories(directory.toPath());
            } catch (Exception e) {
                logger.error("Failed to create client directory", e);
                System.exit(-1);
            }
        });

        initLoader.add(createLoadTask(processorManager));
        initLoader.add(createLoadTask(fontManager));
        initLoader.add(createLoadTask(moduleManager));
        initLoader.add(createLoadTask(commandManager));
        initLoader.add(createLoadTask(settingManager));
        initLoader.add(createLoadTask(itemGroupManager));
        initLoader.add(createLoadTask(configManager));

        initLoader.run();
    }

    /**
     * Called after Minecraft has initialized
     */
    @Override
    public void start() {
        while (!initLoader.finished()) {
            logger.info("Waiting...");
        }

        try {
            final File configFile = new File(directory, "imgui");
            imGui = new ImGuiImpl.ImGuiBuilder(mc.getWindow().getHandle(), configFile.getAbsolutePath()).build();
            ThemeTOMLParser.setup(ImGui.getStyle());
        } catch (Exception e) {
            throw new RuntimeException("Failed to start the client", e);
        }

        try {
            processorManager.start();
            fontManager.start();
            moduleManager.start();
            commandManager.start();
            settingManager.start();
            itemGroupManager.start();
            configManager.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start the client", e);
        }

        loaded = true;
    }

    /**
     * Called when the game is stopping
     */
    public void end() {
        configManager.save();
    }

    @EventHandler
    public final void onGameTitle(GameTitleEvent gameTitleEvent) {
        final String name = random > 0.5 ? LATIN_NAME : CYRILLIC_NAME;
        gameTitleEvent.title = String.format("%s v%s | %s", name, VERSION, gameTitleEvent.title);
    }

    /**
     * Creates a load task from an Initializable object that passes arguments and stops the client on an exception
     *
     * @param initializable The Initializable which shall be initialized
     * @param args The arguments to be passed to the initializable
     * @return Load task
     */
    private Runnable createLoadTask(Initializable initializable, String... args) {
        return () -> {
            try {
                initializable.init(args);
            } catch (Exception e) {
                logger.error("Failed to load {}", initializable.getClass().getSimpleName(), e);
                System.exit(-1);
            }
        };
    }

    /**
     * Implementation of the singleton design pattern
     *
     * @return The singleton instance
     */
    public static Argon getInstance() {
        Argon result = instance;
        if (result != null) {
            return result;
        }
        synchronized(Argon.class) {
            if (instance == null) {
                instance = new Argon();
            }
            return instance;
        }
    }

    public static class Static {
        public static String[] args;
    }

}
