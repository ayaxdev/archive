package com.skidding.atlas.hud.impl;

import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.hud.HUDFactory;
import com.skidding.atlas.hud.HUDElement;
import com.skidding.atlas.hud.util.Side;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.tracker.LatencyTracker;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.text.TextFormatter;
import com.skidding.atlas.util.text.TextFormatterBuilder;

import java.util.function.Supplier;

public class TextElement extends HUDFactory {

    public TextElement() {
        super("Text", "A customizable text element");
    }

    public HUDElement build(String name, float x, float y, int priority, Side side, String defaultText) {
        return new HUDElement(name, description, enabled, x, y, priority, side) {
            private final LatencyTracker latencyTracker = ProcessorManager.getSingleton().getByClass(LatencyTracker.class);

            public final SettingFeature<String> text = text("Text", defaultText).build();
            public final SettingFeature<FontRendererValue> font = font("Font", "Arial", "Regular", 19).build();
            public final SettingFeature<Boolean> drawShadow = check("Draw shadow", true).build();
            public final SettingFeature<Integer> color = color("Color", 255, 255, 255, 255).build();

            final TextFormatter textFormatter = new TextFormatterBuilder()
                    .addDefaultFunctions()
                    .addDefaultPlaceholders()
                    .build();

            @Override
            public void draw() {
                if(drawShadow.getValue())
                    font.getValue().fontRenderer().drawStringWithShadow(getRenderText(), 0, 0, color.getValue());
                else
                    font.getValue().fontRenderer().drawString(getRenderText(), 0, 0, color.getValue(), false);
            }

            @Override
            public void updateSize() {
                this.width = font.getValue().fontRenderer().getStringWidth(getRenderText());
                this.height = font.getValue().fontRenderer().getHeight();
            }

            @Override
            public String getPreview() {
                return getRenderText();
            }

            public String getRenderText() {
                return textFormatter.get(text.getValue());
            }
        };
    }

    private String insertPlaceholder(String input, String name, Supplier<Object> stringSupplier) {
        final String placeholderTrigger = STR."%\{name.toUpperCase().replace(" ", "_")}%".replace("%%", "%");

        try {
            return input.replace(placeholderTrigger, (stringSupplier.get() instanceof Float || stringSupplier.get() instanceof Double) ? String.format("%.2f", ((Number) stringSupplier.get()).floatValue()) : stringSupplier.get().toString());
        } catch (Exception e) {
            return input.replace(placeholderTrigger, "Unknown");
        }
    }

    @Override
    public HUDElement build(String name, float x, float y, int priority, Side side) {
        return this.build(name, x, y, priority, side, "Text");
    }

}
