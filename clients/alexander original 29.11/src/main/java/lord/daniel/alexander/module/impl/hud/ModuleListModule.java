package lord.daniel.alexander.module.impl.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.Render2DEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.ColorFormatSetting;
import lord.daniel.alexander.settings.impl.mode.FontValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.settings.impl.string.StringValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.java.StringUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.shader.render.ingame.ShaderRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "ModuleList", aliases = {"ArrayList", "ModulesList"}, enumModuleType = EnumModuleType.HUD)
public class ModuleListModule extends AbstractModule {

    private final FontValue fontRenderer = new FontValue("Font", this);
    private final BooleanValue drawShadow = new BooleanValue("DrawShadow", this, true);
    private final ClientColorValue color = new ClientColorValue("TextColor", this);
    private final BooleanValue renderBackground = new BooleanValue("RenderBackground", this, true);
    private final ClientColorValue backgroundColor = new ClientColorValue("BackgroundColor", this, new Color(0, 0, 0, 120)).addVisibleCondition(renderBackground::getValue);
    private final BooleanValue overrideBloomColor = new BooleanValue("OverrideBloomColor", this, false);
    private final ClientColorValue bloomBackgroundColor = new ClientColorValue("BloomBackgroundColor", this, new Color(0, 0, 0, 255)).addVisibleCondition(overrideBloomColor::getValue);
    private final NumberValue<Float> addWidth = new NumberValue<>("AddWidth", this, 4f, 0f, 10f, 1);
    private final NumberValue<Float> addHeight = new NumberValue<>("AddHeight", this, 4f, 0f, 10f, 1);
    private final NumberValue<Float> textAddY = new NumberValue<>("TextAddY", this, 0f, -5f, 5f, 1, false);
    private final NumberValue<Float> textAddX = new NumberValue<>("TextAddX", this, 0f, -5f, 5f, 1, false);
    private final NumberValue<Float> xOffset = new NumberValue<>("XOffset", this, 0f, 0f, 50f);
    private final NumberValue<Float> yOffset = new NumberValue<>("YOffset", this, 0f, 0f, 50f);
    private final BooleanValue customModuleName = new BooleanValue("CustomModuleName", this, false);
    private final BooleanValue suffix = new BooleanValue("Suffix", this, true);
    private final StringValue moduleName = new StringValue("ModuleName", this, "%MODULE_NAME% - %FORMAT_WHITE%[%MODULE_SUFFIX%]").addVisibleCondition(customModuleName::getValue).addVisibleCondition(suffix::getValue);
    private final StringModeValue suffixNameConnector = new StringModeValue("SuffixNameConnector", this, "n - s", new String[]{"n s", "n, s", "n - s", "n # s", "n / s", "n \\ s", "n < s", "n > s", "n ! s"}).addVisibleCondition(() -> !customModuleName.getValue()).addVisibleCondition(suffix::getValue);;
    private final StringModeValue suffixSurround = new StringModeValue("SuffixSurround", this, "[s]", new String[]{"s", "(s)" ,"[s]", "{s}", "/s\\", "\\s/", "#s#", "!s!", "'s'", "\"s\""}).addVisibleCondition(() -> !customModuleName.getValue()).addVisibleCondition(suffix::getValue);;
    private final ColorFormatSetting suffixColorFormat = new ColorFormatSetting("SuffixColorFormat", this, EnumChatFormatting.WHITE).addVisibleCondition(() -> !customModuleName.getValue()).addVisibleCondition(suffix::getValue);;
    private final BooleanValue colorConnector = new BooleanValue("ColorSuffixConnector", this, true).addVisibleCondition(() -> !customModuleName.getValue()).addVisibleCondition(suffix::getValue);;
    private final StringModeValue suffixConnector = new StringModeValue("SuffixConnector", this, "s, s", new String[]{"s s", "s, s", "s - s", "s # s", "s / s", "s \\ s", "s < s", "s > s", "s ! s"}).addVisibleCondition(suffix::getValue);;

    @EventLink
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        final FontRenderer fontRenderer = this.fontRenderer.getValue();

        ArrayList<AbstractModule> modules = new ArrayList<>(ModuleStorage.getModuleStorage().getList());

        modules.sort(Comparator.comparingInt(o -> fontRenderer.getStringWidth(getModuleName((AbstractModule) o))).reversed());

        ShaderRenderer.render(shaders -> {
            float y = yOffset.getValue();
            float x = render2DEvent.getScaledResolution().getScaledWidth() - xOffset.getValue();
            int counter = 0;

            for(AbstractModule abstractModule : modules) {
                if(!abstractModule.isEnabled())
                    continue;

                final String moduleName = getModuleName(abstractModule);

                final float textWidth = fontRenderer.getStringWidth(moduleName),
                        textHeight = fontRenderer.FONT_HEIGHT;

                final float moduleWidth = textWidth + addWidth.getValue(),
                        moduleHeight = fontRenderer.FONT_HEIGHT + addHeight.getValue();

                if(renderBackground.getValue())
                    RenderUtil.drawRect(x - moduleWidth, y, moduleWidth, moduleHeight, shaders && overrideBloomColor.getValue() ? bloomBackgroundColor.getValue(counter) : backgroundColor.getValue(counter));

                if(!shaders)
                    if(drawShadow.getValue())
                        fontRenderer.drawStringWithShadow(moduleName, x - moduleWidth / 2 - textWidth / 2 + textAddX.getValue(), y + moduleHeight / 2 - textHeight / 2 + textAddY.getValue(), color.getValue(counter).getRGB());
                    else
                        fontRenderer.drawString(moduleName, x - moduleWidth / 2 - textWidth / 2 + textAddX.getValue(), y + moduleHeight / 2 - textHeight / 2 + textAddY.getValue(), color.getValue(counter).getRGB());

                y += moduleHeight;
                counter++;
            }
        });
    };

    public String getModuleName(AbstractModule abstractModule) {
        if(abstractModule.getSuffix() != null && suffix.getValue()) {
            String suffix = String.join(suffixConnector.getValue().replace("s", ""), abstractModule.getSuffix());

            if(this.customModuleName.getValue()) {
                return StringUtil.format(this.moduleName.getValue().replace("%MODULE_NAME%", abstractModule.getDisplayName()).replace("%MODULE_SUFFIX%", suffix));
            } else {
                final String finalName = abstractModule.getDisplayName() + (colorConnector.getValue() ? suffixColorFormat.getFormat().toString() : "");
                final String finalSuffix = suffixSurround.getValue().replace("s", (!colorConnector.getValue() ? suffixColorFormat.getFormat().toString() : "") + suffix);
                final String connector = suffixNameConnector.getValue().substring(1, suffixNameConnector.getValue().length() - 1);
                return finalName + connector + finalSuffix;
            }
        } else {
            return abstractModule.getDisplayName();
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
