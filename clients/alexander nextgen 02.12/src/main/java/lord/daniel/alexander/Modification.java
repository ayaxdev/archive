package lord.daniel.alexander;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import io.github.nevalackin.radbus.PubSub;
import lombok.Getter;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.client.BackgroundRunEvent;
import lord.daniel.alexander.handler.event.EventHandler;
import lord.daniel.alexander.handler.input.ClickHandler;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.storage.impl.FileStorage;
import lord.daniel.alexander.storage.impl.FontStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.run.MultiThreadedUtil;
import net.minecraft.util.Session;
import org.lwjgl.Version;
import org.lwjglx.opengl.Display;

import java.io.File;

/**
 * Written by Daniel. on 20/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
public class Modification implements IMinecraft {

    public static final String NAME = "Alexander", FULL_NAME = "Alexander Recode", DEVELOPER = "Daniel.";
    public static final String ASSETS_DIR = "alexander", FULL_ASSETS_DIR = "assets/minecraft/alexander",
            FONTS_DIR = ASSETS_DIR + "/fonts/", FULL_FONTS_DIR = FULL_ASSETS_DIR + "/fonts/",
            SHADERS_DIR = ASSETS_DIR + "/shaders/", FULL_SHADERS_DIR = FULL_ASSETS_DIR + "/shaders/",
            BACKGROUNDS_DIR = ASSETS_DIR + "/shaders/fragment/backgrounds", FULL_BACKGROUNDS_DIR = FULL_ASSETS_DIR + "/shaders/fragment/backgrounds",
            TEXTURES_DIR = ASSETS_DIR + "/textures/", FULL_TEXTURES_DIR = FULL_ASSETS_DIR + "/textures/",
            TEXTS_DIR = ASSETS_DIR + "/texts/", FULL_TEXTS_DIR = FULL_ASSETS_DIR + "/texts/";
    public static final int VERSION = 1;

    @Getter
    private static final Modification modification = new Modification();
    private final PubSub<Event> pubSub = PubSub.newInstance(System.out::println);

    private File rootDir, fileDir, configDir;

    public void startClient() {
        Display.setTitle(String.format("%s b%d (LWJGL %S)", FULL_NAME, VERSION, Version.getVersion()));

        setupDirectories(FULL_NAME);

        ModuleStorage.setModuleStorage(new ModuleStorage());
        FileStorage.setFileStorage(new FileStorage());
        FontStorage.setFontStorage(new FontStorage());

        ModuleStorage.getModuleStorage().init();
        FileStorage.getFileStorage().init();
        FontStorage.getFontStorage().init();

        getPubSub().subscribe(new EventHandler());
        getPubSub().subscribe(new ClickHandler());

        Runtime.getRuntime().addShutdownHook(new Thread(this::endClient));

        MultiThreadedUtil.runAsync(() -> {
            while (true) {
                Modification.getModification().getPubSub().publish(new BackgroundRunEvent());
            }
        }, "BackgroundRunEventThread");

        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials("danielthegreat@seznam.cz", "^A5YgScjd>+m)+z:IH'vT\"4z0;6n4Pct`A<MtD3c;n*k\\)rOPrW~JF]JYjpQ|Vh_wmN/a-6\"80*fLQ:OwBO-*)j\\t`dZM}j2lE-");
            mc.session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupDirectories(String name) {
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

    public void endClient() {
        FileStorage.getFileStorage().save();
    }

}
