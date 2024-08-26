package ja.tabio.argon.font.manager;

import ja.tabio.argon.Argon;
import ja.tabio.argon.font.ClientFontRenderer;
import ja.tabio.argon.interfaces.Initializable;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FontManager implements Initializable {

    private final List<FontEntry> unloaded = new ArrayList<>();
    private final List<FontEntry> fonts = new ArrayList<>();

    @Override
    public void init(String[] args) {
        unloaded.add(FontEntry.builder().build());
    }

    @Override
    public void start() {
        update();
    }

    private void update() {
        for (int i = 0; i < unloaded.size(); i++) {
            try {
                final FontEntry unloadedEntry = unloaded.get(i);
                unloadedEntry.generate();
                fonts.add(unloadedEntry);
                unloaded.remove(i);
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed generating font");
            }
        }
    }

    public ClientFontRenderer get(FontEntry fontEntry) {
        if (fonts.contains(fontEntry))
            return fontEntry.clientFontRenderer;

        final FontEntry newFontEntry = fonts.stream()
                .filter(it -> it.equals(fontEntry))
                .findFirst().orElse(null);

        if (newFontEntry != null) {
            return newFontEntry.clientFontRenderer;
        } else {
            unloaded.add(fontEntry);

            update();

            return get(fontEntry);
        }
    }

    public static class FontEntry {

        public ClientFontRenderer clientFontRenderer;

        public final String family, type;
        public final boolean otf;
        public final float size;
        public final int narrowBy;
        public final int charactersPerPage, paddingBetweenCharacter;

        public FontEntry(String family, String type, boolean otf, float size, int narrowBy, int charactersPerPage, int paddingBetweenCharacter, ClientFontRenderer clientFontRenderer) {
            this.family = family;
            this.type = type;
            this.otf = otf;
            this.size = size;
            this.narrowBy = narrowBy;
            this.charactersPerPage = charactersPerPage;
            this.paddingBetweenCharacter = paddingBetweenCharacter;
            this.clientFontRenderer = clientFontRenderer;
        }

        public static FontEntryBuilder builder() {
            return new FontEntryBuilder();
        }

        public void generate() throws IOException, FontFormatException {
            final StringBuilder pathBuilder = new StringBuilder("assets/argon/fonts/").append(family).append("/").append(type);
            if (otf)
                pathBuilder.append(".otf");
            else
                pathBuilder.append(".ttf");

            final String path = pathBuilder.toString();
            final float realSize = this.size / 2f;

            final Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Argon.class.getClassLoader().getResourceAsStream(path))).deriveFont(Font.PLAIN, realSize);

            this.clientFontRenderer = new ClientFontRenderer(font, realSize, charactersPerPage, paddingBetweenCharacter, narrowBy);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FontEntry fontEntry = (FontEntry) o;
            return otf == fontEntry.otf && Float.compare(size, fontEntry.size) == 0 && narrowBy == fontEntry.narrowBy && charactersPerPage == fontEntry.charactersPerPage && paddingBetweenCharacter == fontEntry.paddingBetweenCharacter && Objects.equals(clientFontRenderer, fontEntry.clientFontRenderer) && Objects.equals(family, fontEntry.family) && Objects.equals(type, fontEntry.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientFontRenderer, family, type, otf, size, narrowBy, charactersPerPage, paddingBetweenCharacter);
        }

        public static class FontEntryBuilder {
            private String type = "Arial", family = "Regular";
            private float size = 18;
            private boolean otf = false;
            private int narrowBy = 0,
                    charactersPerPage = 256,
                    paddingBetweenCharacter = 5;

            public final FontEntryBuilder type(String name) {
                this.type = name;
                return this;
            }

            public final FontEntryBuilder family(String family) {
                this.family = family;
                return this;
            }

            public final FontEntryBuilder size(float size) {
                this.size = size;
                return this;
            }

            public final FontEntryBuilder otf(boolean otf) {
                this.otf = otf;
                return this;
            }

            public final FontEntryBuilder narrowBy(int narrowBy) {
                this.narrowBy = narrowBy;
                return this;
            }

            public final FontEntryBuilder charactersPerPage(int charactersPerPage) {
                this.charactersPerPage = charactersPerPage;
                return this;
            }

            public final FontEntryBuilder paddingBetweenCharacter(int paddingBetweenCharacter) {
                this.paddingBetweenCharacter = paddingBetweenCharacter;
                return this;
            }

            public final FontEntry build() {
                return new FontEntry(family, type, otf, size, narrowBy, charactersPerPage, paddingBetweenCharacter, null);
            }

            public final ClientFontRenderer get() {
                return Argon.getInstance().fontManager.get(build());
            }
        }

    }

}
