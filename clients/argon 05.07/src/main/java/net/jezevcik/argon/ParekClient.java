package net.jezevcik.argon;

import io.github.racoondog.norbit.EventBus;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.jezevcik.argon.account.repository.AccountRepository;
import net.jezevcik.argon.bind.BindManager;
import net.jezevcik.argon.command.repository.CommandRepository;
import net.jezevcik.argon.event.impl.WindowTitleEvent;
import net.jezevcik.argon.file.repository.FileRepository;
import net.jezevcik.argon.module.repository.ModuleRepository;
import net.jezevcik.argon.processor.repository.ProcessorRepository;
import net.jezevcik.argon.protection.SystemCheck;
import net.jezevcik.argon.system.initialize.InitializeStage;
import net.jezevcik.argon.worker.Worker;
import net.jezevcik.argon.worker.impl.AsynchronousWorker;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ParekClient implements ModInitializer {

	/**
	 * The mod's id. Used for accessing information and creating identifiers.
	 */
	public static final String MOD_ID = "parekclient";

	/**
	 * The mod's metadata, set in fabric.mod.json and pulled using the modid.
	 */
	public static final ModMetadata METADATA = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();

	/**
	 * Original name, changed to a version friendlier for the rest of the world.
	 */
	public static final String REAL_NAME = "Párek";
	/**
	 * New name designed to be friendlier to the rest of the world.
	 */
	public static final String DISPLAY_NAME = "Argon";
	/**
	 * Subtext is usually some joke based on the current update.
	 */
	public static final String SUBTEXT = "Intave bypass edition";
	/**
	 * Version string pulled from metadata (fabric.mod.json).
	 */
	public static final String VERSION = METADATA.getVersion().getFriendlyString();

	/**
	 * Used for logging everything in the client.
	 */
	// TODO: Set up a better logging system, which wouldn't log everything into a single logger.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * An implementation of the singleton design pattern.
	 */
	private static volatile ParekClient instance;

	/**
	 * Asynchronous worker executing startup tasks.
	 */
	private final Worker worker = new AsynchronousWorker("Pre-Minecraft", this);

	/**
	 * Path for reflections, modified by the obfuscator.
	 */
	private final String path = "net.jezevcik.argon";

	/**
	 * Reflections object, used for reflecting with the Reflections library.
	 */
	public final Reflections reflections = new Reflections(path);

	/**
	 * The directory in which client files are stored
	 */
	public final Path dir = Path.of(REAL_NAME);

	/**
	 * Publish events and subscribe to them with the event bus.
	 */
	public final EventBus eventBus = EventBus.threadSafe();
	/**
	 * Register keybindings and actions with the bind manager.
	 */
	public final BindManager bindManager = new BindManager();
	/**
	 * Repository with the alt accounts provided by the user
	 */
	public final AccountRepository accounts = new AccountRepository();
	/**
	 * Repository with the client's modules. Is responsible for loading and accessing processor.
	 */
	public final ProcessorRepository processors = new ProcessorRepository();
	/**
	 * Repository with the client's modules. Is responsible for loading and accessing modules.
	 */
	public final ModuleRepository modules = new ModuleRepository();
	/**
	 * Repository with the client's modules. Is responsible for loading and accessing commands.
	 */
	public final CommandRepository commands = new CommandRepository();
	/**
	 * Repository with the client's data files. Is responsible for saving and loading data.
	 */
	public final FileRepository files = new FileRepository();

	/**
	 * Whether the client has finished loading or not.
	 * Is set to true after all stages of initialization finish.
	 */
	private boolean loaded;

	/**
	 * Ran two times, before Minecraft has loaded, and after Minecraft has loaded.
	 *
	 * @param stage The loading stage
	 */
	public void init(final InitializeStage stage) {
		// Protection temporarily here
		// TODO: better protection
		SystemCheck.run();

		switch (stage) {
            case PRE_MINECRAFT -> {
				eventBus.registerLambdaFactory(path, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

				worker.addTask(() -> {
					try {
						if (Files.exists(dir) && !Files.isDirectory(dir))
							Files.delete(dir);
					} catch (Exception e) {
						throw new RuntimeException("Failed to delete file in-place of client directory", e);
					}

					try {
						Files.createDirectories(dir);
					} catch (Exception e) {
						throw new RuntimeException("Failed to create client directory", e);
					}
				});

				worker.addTask(() -> eventBus.subscribe(this));
				worker.addTask(() -> eventBus.subscribe(bindManager));

				worker.addTask(() -> accounts.init(stage));
				worker.addTask(() -> processors.init(stage));
				worker.addTask(() -> modules.init(stage));
				worker.addTask(() -> commands.init(stage));
				worker.addTask(() -> files.init(stage));

				worker.start();
			}

			case POST_MINECRAFT -> {
				if (worker.getState() != Worker.State.FINISHED) {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO: better error handling
							throw new RuntimeException(e);
						}
					}
				}

				accounts.init(stage);
				processors.init(stage);
				modules.init(stage);
				commands.init(stage);
				files.init(stage);

				loaded = true;
			}
        }
	}

	/**
	 * Called when Minecraft is stopping
	 */
	public void stop() {
		if (loaded)
			files.write();
	}

	/**
	 * Subscribes to the TitleEvent for overriding the game title.
	 *
	 * @param windowTitleEvent Event
	 */
	@EventHandler
	public void onTitle(WindowTitleEvent windowTitleEvent) {
		windowTitleEvent.title = String.format("%s Client | v%s | %s", DISPLAY_NAME, VERSION, windowTitleEvent.title);
	}

	/**
	 * Implementation of the singleton design pattern.
	 *
	 * @return The mod object's instance
	 */
	public static ParekClient getInstance() {
		ParekClient result = instance;

		if (result != null) {
			return result;
		}

		synchronized(ParekClient.class) {
			if (instance == null) {
				instance = new ParekClient();
			}
			return instance;
		}
	}

	// Don't use this

	@Override
	public void onInitialize() { }
}