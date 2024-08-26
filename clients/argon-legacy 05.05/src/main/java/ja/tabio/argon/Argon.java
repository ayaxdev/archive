package ja.tabio.argon;

import de.florianmichael.rclasses.common.FileUtils;
import io.github.racoondog.norbit.EventBus;
import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.config.manager.ConfigManager;
import ja.tabio.argon.event.impl.BackgroundEvent;
import ja.tabio.argon.event.impl.GameTitleEvent;
import ja.tabio.argon.font.manager.CFontManager;
import ja.tabio.argon.interfaces.IClientInitializeable;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.loader.Loader;
import ja.tabio.argon.loader.impl.AsyncLoader;
import ja.tabio.argon.module.manager.ModuleManager;
import ja.tabio.argon.processor.manager.ProcessorManager;
import ja.tabio.argon.setting.manager.SettingManager;
import net.minecraft.realms.RealmsSharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

public class Argon implements IClientInitializeable, IMinecraft {

    private static volatile Argon instance;

    public final static String LATIN_NAME = "Argon",
                        CYRILLIC_NAME = "Аргон";
    public final static String VERSION = "1.0.0";

    private final Loader initLoader = new AsyncLoader();

    public final File directory = new File(System.getProperty("user.home"), ".argon");
    public final double random = ThreadLocalRandom.current().nextDouble();
    public final Logger logger = LoggerFactory.getLogger(CYRILLIC_NAME);
    public final EventBus eventBus = EventBus.threadSafe();
    public final ProcessorManager processorManager = new ProcessorManager();
    public final CFontManager cFontManager = new CFontManager();
    public final ModuleManager moduleManager = new ModuleManager();
    public final SettingManager settingManager = new SettingManager();
    public final ConfigManager configManager = new ConfigManager();

    @Override
    public void init() {
        initLoader.add(() -> {
            eventBus.registerLambdaFactory("ja.tabio.argon",
                (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
            eventBus.subscribe(Argon.this);
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
        initLoader.add(createLoadTask(cFontManager));
        initLoader.add(createLoadTask(moduleManager));
        initLoader.add(createLoadTask(settingManager));
        initLoader.add(createLoadTask(configManager));

        initLoader.run();
    }

    @Override
    public void start() {
        while (!initLoader.finished()) {
            logger.info("Waiting...");
        }

        try {
            processorManager.start();
            cFontManager.start();
            moduleManager.start();
            settingManager.start();
            configManager.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start the client", e);
        }

        final Thread backgroundThread = new Thread(() -> {
            while (mc.running)
                try {
                    Argon.getInstance().eventBus.post(new BackgroundEvent());
                } catch (Exception e) {
                    Argon.getInstance().logger.error("Failed to post background event", e);
                }
        });
        backgroundThread.start();
    }

    public void stop() {
        try {
            configManager.stop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stop the client", e);
        }
    }

    private Runnable createLoadTask(IClientInitializeable clientInitializeable) {
        return () -> {
            try {
                clientInitializeable.init();
            } catch (Exception e) {
                logger.error("Failed to load {}", clientInitializeable.getClass().getSimpleName(), e);
                System.exit(-1);
            }
        };
    }

    @EventHandler
    public final void onGameTitle(GameTitleEvent gameTitleEvent) {
        final String name = random > 0.5 ? LATIN_NAME : CYRILLIC_NAME;
        gameTitleEvent.title = String.format("%s v%s | Running on Minecraft %s", name, VERSION, RealmsSharedConstants.VERSION_STRING);
    }

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

    public interface IArgonAccess {

        default Argon getInstance() {
            return Argon.getInstance();
        }

        default Logger getLogger() {
            return getInstance().logger;
        }

        default EventBus getBus() {
            return getInstance().eventBus;
        }

        default CFontManager getFontManager() {
            return getInstance().cFontManager;
        }

        default ModuleManager getModuleManager() {
            return getInstance().moduleManager;
        }

        default SettingManager getSettingManager() {
            return getInstance().settingManager;
        }

    }

}
