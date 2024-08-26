package ja.tabio.argon.component.colors;

import java.awt.*;

public enum ColorPalette {
    FATALITY_FIRST(new Color(54, 41, 160)),
    FATALITY_SECOND(new Color(147, 28, 95));
    
    public final Color color;

    ColorPalette(Color color) {
        this.color = color;
    }
}
