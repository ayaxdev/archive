package com.skidding.atlas.hud.impl;

import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.hud.HUDElement;
import com.skidding.atlas.hud.HUDFactory;
import com.skidding.atlas.hud.util.Side;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.impl.DecelerateAnimation;
import com.skidding.atlas.util.pair.impl.Position2D;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

public class ModuleListElement extends HUDFactory {
    
    public ModuleListElement() {
        super("ModuleList", "Displays a customizable list of modules");
    }

    @Override
    public HUDElement build(String name, float x, float y, int priority, Side side) {
        return new ModuleList(name, description, enabled, x, y, priority, side);
    }

    public static class ModuleList extends HUDElement {

        private final ArrayList<ModuleListEntry> entries = new ArrayList<>();

        // Text
        public final SettingFeature<FontRendererValue> fontMode = font("Font", "Roboto", "Regular", 18).build();
        public final SettingFeature<String> caseMode = mode("Case mode", "Default", new String[]{"Default", "Lowercase", "Uppercase"}).build();
        public final SettingFeature<Boolean> drawShadow = check("Draw shadow", true).build();
        public final SettingFeature<Boolean> spaceOut = check("Space out", false).build();
        public final SettingFeature<Integer> textColor = color("Text color", 255, 255, 255, 255).build();
        public final SettingFeature<Float> animationSpeed = slider("Animation speed", 500, 0, 1000, 0)
                .addValueChangeListeners((setting, newValue, oldValue, pre) -> {
                    if(!Objects.equals(newValue, oldValue) && pre)
                        entries.clear();
                })
                .build();

        public final SettingFeature<Float> textX = slider("+Text X", 0, 0, 50, 1).build();
        public final SettingFeature<Float> textY = slider("+Text Y", 0.3f, 0, 50, 1).build();
        public final SettingFeature<Float> addModuleWidth = slider("+Width", 3.3f, 0, 50, 1).build();
        public final SettingFeature<Float> addModuleHeight = slider("+Height", 0.8f, 0, 50, 1).build();

        // Background
        public final SettingFeature<Boolean> drawBackground = check("Draw background", true).build();
        public final SettingFeature<Integer> backgroundColor = color("Background color", 0, 0, 0, 159)
                .addDependency(drawBackground).build();

        // Sorting
        public final SettingFeature<Boolean> sort = check("Sort", true).build();
        public final SettingFeature<Boolean> reverseSorting = check("Reverse sorting", true).addDependency(sort).build();
        public final SettingFeature<String> sortingMode = mode("Sorting mode", "Name", new String[]{"Name", "Category"}).addDependency(sort).build();
        public final SettingFeature<String> stringSortingMode = mode("Sort strings by", "Font size", new String[]{"Alphabetical", "Font size", "Length"}).addDependency(sort).build();

        public ModuleList(String name, String description, Supplier<Boolean> enabled, float x, float y, int priority, Side side) {
            super(name, description, enabled, x, y, priority, side);
        }

        @Override
        public void draw() {
            final ModuleManager moduleManager = ModuleManager.getSingleton();

            final FontRenderer font = this.fontMode.getValue().fontRenderer();

            final List<ModuleFeature> modules = new ArrayList<>(moduleManager.getFeatures());

            if (sort.getValue()) {
                final Comparator<ModuleFeature> moduleComparator = getModuleFeatureComparator(font);

                modules.sort(moduleComparator);
            }

            float updatedYPosition = 0;

            for (ModuleFeature module : modules) {
                final float nameWidth = font.getStringWidth(getModuleName(module));

                // Declaring base values
                float moduleX = -nameWidth,
                        moduleY = updatedYPosition,
                        moduleHeight = getHeight(font);

                // Removing the additional with and height from base x and y
                moduleX -= addModuleWidth.getValue();
                moduleY -= addModuleHeight.getValue();

                ModuleListEntry foundEntry = null;

                for (ModuleListEntry entryItem : this.entries) {
                    if (entryItem.moduleFeature() == module) {
                        foundEntry = entryItem;
                        break;
                    }
                }

                if (foundEntry == null && module.isEnabled()) {
                    foundEntry = new ModuleListEntry(module, new Position2D(moduleX, moduleY), new DecelerateAnimation(animationSpeed.getValue().intValue(), 100, Direction.BACKWARDS));

                    updatedYPosition += moduleHeight;
                    this.entries.add(foundEntry);
                } else if (foundEntry != null) {
                    final ModuleListEntry newEntry = new ModuleListEntry(module, new Position2D(moduleX, moduleY), foundEntry.decelerateAnimation());

                    if (module.isEnabled() || !foundEntry.decelerateAnimation().isDone()) {
                        this.entries.set(this.entries.indexOf(foundEntry), newEntry);
                        updatedYPosition += moduleHeight;
                    }
                }
            }

            for (int i = 0; i < this.entries.size(); i++) {
                final ModuleListEntry entry = this.entries.get(i);

                if (entry.canBeClosed())
                    this.entries.remove(entry);
            }

            final ScaledResolution scaledResolution = new ScaledResolution(mc);

            for(ModuleListEntry entry : this.entries) {
                if (entry.moduleFeature.isEnabled()) {
                    if (entry.decelerateAnimation.getDirection() == Direction.BACKWARDS) {
                        entry.decelerateAnimation.setDirection(Direction.FORWARDS);
                        entry.decelerateAnimation.reset();
                    }
                } else {
                    entry.decelerateAnimation.setDirection(Direction.BACKWARDS);
                }
            }

            final Map<ModuleListEntry, Double> animationOutMap = new LinkedHashMap<>();

            for (ModuleListEntry entry : this.entries) {
                animationOutMap.put(entry, entry.decelerateAnimation().getOutput());
            }

            // A dirty hack to get glTranslated working
            end();

            ShaderRenderer.INSTANCE.drawAndRun(shaders -> {
                for (ModuleListEntry entry : this.entries) {
                    final ModuleFeature module = entry.moduleFeature();

                    float moduleWidth = getWidth(font, module);

                    double x = switch (side.horizontal()) {
                        case LEFT, MIDDLE_LEFT -> getRenderX();
                        case RIGHT, MIDDLE_RIGHT -> getRenderX() + width - moduleWidth;
                    };

                    float moduleX = switch (side.horizontal()) {
                        case LEFT, MIDDLE_LEFT -> (float) ((x * animationOutMap.get(entry)) / 100);
                        case RIGHT, MIDDLE_RIGHT -> scaledResolution.getScaledWidth() - (float) ((((scaledResolution.getScaledWidth() - x) * animationOutMap.get(entry)) / 100));
                    };

                    // Declaring base values
                    float moduleY = (float) (getRenderY() + entry.target.getY()),
                            moduleHeight = font.getHeight() + addModuleHeight.getValue();

                    if (drawBackground.getValue()) {
                        DrawUtil.drawRectRelative(moduleX, moduleY, moduleWidth, moduleHeight,
                                new Color(backgroundColor.getValue() >> 16 & 0xFF,
                                        backgroundColor.getValue() >> 8 & 0xFF,
                                        backgroundColor.getValue() & 0xFF, shaders ? 255 : backgroundColor.getValue() >> 24 & 0xFF).getRGB());
                    }

                    if (!shaders)
                        if (drawShadow.getValue())
                            font.drawXYCenteredStringWithShadow(getModuleName(module), moduleX + moduleWidth / 2 - 0.5f + textX.getValue(), moduleY + moduleHeight / 2 + textY.getValue(), textColor.getValue());
                        else
                            font.drawXYCenteredString(getModuleName(module), moduleX + moduleWidth / 2 - 0.5f + textX.getValue(), moduleY + moduleHeight / 2 + textY.getValue(), textColor.getValue());
                }
            });

            begin();
        }

        private float getWidth(FontRenderer font, ModuleFeature module) {
            return (float) font.getStringWidth(getModuleName(module)) + addModuleWidth.getValue();
        }

        private float getHeight(FontRenderer font) {
            return font.getHeight() + addModuleHeight.getValue();
        }

        @Override
        public void updateSize() {
            final FontRenderer fontRenderer = this.fontMode.getValue().fontRenderer();

            float width = 100;
            float height = 0;

            for(ModuleFeature moduleFeature : ModuleManager.getSingleton().getFeatures()) {
                final float moduleWidth = getWidth(fontRenderer, moduleFeature);

                width = Math.max(width, moduleWidth);

                if(moduleFeature.isEnabled())
                    height += getHeight(fontRenderer);
            }

            this.width = width;
            this.height = height;
        }

        private String getModuleName(ModuleFeature moduleFeature) {
            String oldModuleName = moduleFeature.getName();
            String spacedModuleName = spaceOut.getValue() ? oldModuleName.replaceAll("([A-Z]+)", " $1") : oldModuleName;

            spacedModuleName = spacedModuleName.trim();

            switch (caseMode.getValue()) {
                default -> {
                    return spacedModuleName;
                }
                case "Lowercase" -> {
                    return spacedModuleName.toLowerCase();
                }
                case "Uppercase" -> {
                    return spacedModuleName.toUpperCase();
                }
            }
        }

        private Comparator<ModuleFeature> getModuleFeatureComparator(FontRenderer font) {
            final Comparator<ModuleFeature> moduleComparator = new Comparator<>() {
                @Override
                public int compare(ModuleFeature o1, ModuleFeature o2) {
                    String mod1 = getSortString(o1);
                    String mod2 = getSortString(o2);

                    return switch (stringSortingMode.getValue()) {
                        case "Alphabetical" -> mod1.compareTo(mod2);
                        case "Length" -> Integer.compare(mod1.length(), mod2.length());
                        case "Font size" -> Integer.compare(font.getStringWidth(mod1), font.getStringWidth(mod2));
                        default -> -1;
                    };

                }

                private String getSortString(ModuleFeature module) {
                    return switch (sortingMode.getValue()) {
                        case "Name" -> getModuleName(module);
                        case "Category" -> module.moduleCategory.name();
                        default -> throw new IllegalStateException(STR."Unexpected value: \{sortingMode.getValue()}");
                    };
                }
            };

            if (reverseSorting.getValue()) {
                return moduleComparator.reversed();
            } else {
                return moduleComparator;
            }
        }

    }

    private record ModuleListEntry(ModuleFeature moduleFeature, Position2D target,
                                   DecelerateAnimation decelerateAnimation) {

        public boolean canBeClosed() {
            return !moduleFeature().isEnabled() && decelerateAnimation.finished(Direction.BACKWARDS);
        }

    }
    
}
