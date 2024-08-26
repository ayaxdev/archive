package lord.daniel.alexander.alexandergui.impl.altmanager.utils;

import java.net.URI;

public class SystemUtils {
    public static void openWebLink(final URI url) {
        try {
            final Class<?> desktop = Class.forName("java.awt.Desktop");
            final Object object = desktop.getMethod("getDesktop", new Class[0]).invoke(null);
            desktop.getMethod("browse", new Class[]{URI.class}).invoke(object, url);
        } catch (Throwable throwable) {
            System.err.println(throwable.getCause().getMessage());
        }
    }

}
