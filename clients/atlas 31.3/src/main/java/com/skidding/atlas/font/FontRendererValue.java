package com.skidding.atlas.font;

import net.minecraft.client.gui.FontRenderer;

import java.util.Objects;

public record FontRendererValue(String family, String fontType, float size, FontRenderer fontRenderer) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontRendererValue that = (FontRendererValue) o;
        return Float.compare(size, that.size) == 0 && Objects.equals(family, that.family) && Objects.equals(fontType, that.fontType) && Objects.equals(fontRenderer, that.fontRenderer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, fontType, size, fontRenderer);
    }
}
