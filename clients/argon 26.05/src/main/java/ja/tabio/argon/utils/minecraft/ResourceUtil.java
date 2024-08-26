package ja.tabio.argon.utils.minecraft;

import ja.tabio.argon.Argon;
import ja.tabio.argon.utils.math.random.Randomization;
import net.minecraft.util.Identifier;

public class ResourceUtil {

    public static Identifier getRandomIdentifier() {
        return new Identifier(Argon.MOD_ID, String.format("temp/%s", Randomization.JAVA.algorithm.getRandomString(32)));
    }

}
