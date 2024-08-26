package lord.daniel.alexander.storage.impl;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.module.impl.options.FontRendererOptionModule;
import lord.daniel.alexander.storage.Storage;
import lord.daniel.alexander.util.os.FileUtils;
import lord.daniel.alexander.util.render.font.CFont;
import lord.daniel.alexander.util.render.font.CFontRenderer;
import lord.daniel.alexander.util.render.font.modern.SmoothFontRenderer;
import lord.daniel.alexander.util.render.font.old.SharpFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */

@Getter
public class FontStorage extends Storage<CFont> {

    @Getter
    @Setter
    private static FontStorage fontStorage;

    private final Map<String, List<String>> savedFontTypes = new HashMap<>();

    private FontRendererOptionModule fontRendererOptionModule;

    @Override
    public void init() {
        try {
            for (String folder : FileUtils.listFilesInResources(Modification.FULL_FONTS_DIR)) {
                for (String font : FileUtils.listFilesInResources(Modification.FULL_FONTS_DIR + "/" + folder)) {
                    String fontName = font.substring(0, font.length() - 4);
                    if (savedFontTypes.containsKey(folder)) {
                        savedFontTypes.get(folder).add(fontName);
                    } else {
                        savedFontTypes.put(folder, new ArrayList<>(List.of(fontName)));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Input/Output exception: ");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void add(String family, String type, float size, boolean otf) throws IOException, FontFormatException {
        final ResourceLocation fontLocation = new ResourceLocation(Modification.FONTS_DIR + family + "/" + type + (otf ? ".otf" : ".ttf"));
        final Font finalFont = Font.createFont(Font.PLAIN, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream()).deriveFont(size);
        this.add(new CFont(finalFont, family, type, size));
    }

    private final ArrayList<String> currentlyBeingMade = new ArrayList<>();

    public CFont get(String family, float size) {
        return this.get(family, "Regular", size);
    }

    public CFont get(String family, String type, float size) {

        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        try {
            return this.getList().stream().filter(cFont -> cFont.getFamily().equals(family) && cFont.getType().equals(type) && cFont.getSize() == size).findFirst().orElseGet(() -> {
                System.out.printf("Failed to find font, creating now: %s %s %s\n", family, type, size);
                try {
                    FontStorage.getFontStorage().add(family, type, size, false);
                } catch (Exception e1) {
                    try {
                        FontStorage.getFontStorage().add(family, type, size, true);
                    } catch (Exception e2) {
                        System.out.printf("Failed to create font: %s %s %s\n", family, type, size);
                        e2.printStackTrace();
                        throw new RuntimeException();
                    }
                }
                return get(family, type, size);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}