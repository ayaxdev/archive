package ja.tabio.argon.config.impl;

import com.google.gson.JsonObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.RegisterConfig;
import ja.tabio.argon.setting.impl.*;

@RegisterConfig
public class SettingConfig extends Config {

    public SettingConfig() {
        super("settings", true);
    }

    @Override
    protected JsonObject get() {
        final JsonObject settingsObject = new JsonObject();

        Argon.getInstance().settingManager.settingMap.forEach((key, value) -> {
            final JsonObject listObject = new JsonObject();

            value.forEach(setting -> {
                final JsonObject settingObject = switch (setting) {
                    case BooleanSetting booleanSetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("enabled", booleanSetting.getValue());

                        yield object;
                    }

                    case ColorSetting colorSetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("red", colorSetting.getValue().red());
                        object.addProperty("green", colorSetting.getValue().green());
                        object.addProperty("blue", colorSetting.getValue().blue());
                        object.addProperty("alpha", colorSetting.getValue().alpha());

                        yield object;
                    }

                    case KeySetting keySetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("key", keySetting.getValue());

                        yield object;
                    }

                    case ModeSetting modeSetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("selected", modeSetting.getValue());

                        yield object;
                    }

                    case MultiSetting multiSetting -> {
                        final JsonObject object = new JsonObject();

                        for (String s : multiSetting.modes) {
                            object.addProperty(s, multiSetting.getValue().contains(s));
                        }

                        yield object;
                    }

                    case NumberSetting numberSetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("value", numberSetting.getValue());

                        yield object;
                    }

                    case TextInputSetting textInputSetting -> {
                        final JsonObject object = new JsonObject();

                        object.addProperty("text", textInputSetting.getValue());

                        yield object;
                    }

                    default -> new JsonObject();
                };

                listObject.add(setting.getUniqueIdentifier(), settingObject);
            });

            settingsObject.add(key.toString(), listObject);
        });

        return settingsObject;
    }

    @Override
    protected void set(JsonObject jsonObject) {
        Argon.getInstance().settingManager.settingMap.forEach((key, value) -> {
            if (!jsonObject.has(key.toString()))
                return;

            final JsonObject listObject = jsonObject.getAsJsonObject(key.toString());

            value.forEach(setting -> {
                if (!listObject.has(setting.getUniqueIdentifier()))
                    return;

                final JsonObject settingObject = listObject.getAsJsonObject(setting.getUniqueIdentifier());

                switch (setting) {
                    case BooleanSetting booleanSetting -> {
                        if (!settingObject.has("enabled"))
                            return;

                        booleanSetting.setValue(settingObject.get("enabled").getAsBoolean());
                    }

                    case ColorSetting colorSetting -> {
                        if (!settingObject.has("red") ||
                                !settingObject.has("green") ||
                                !settingObject.has("blue") ||
                                !settingObject.has("alpha"))
                            return;

                        final ColorSetting.ColorData colorData = new ColorSetting.ColorData(
                            settingObject.get("red").getAsInt(),
                            settingObject.get("green").getAsInt(),
                            settingObject.get("blue").getAsInt(),
                            settingObject.get("alpha").getAsInt()
                        );

                        try {
                            colorSetting.setValue(colorData);
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    case KeySetting keySetting -> {
                        if (!settingObject.has("key"))
                            return;

                        try {
                            keySetting.setValue(settingObject.get("key").getAsInt());
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    case ModeSetting modeSetting -> {
                        if (!settingObject.has("selected"))
                            return;

                        try {
                            modeSetting.setValue(settingObject.get("selected").getAsString());
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    case MultiSetting multiSetting -> {
                        multiSetting.getValue().clear();

                        try {
                            for (String s : multiSetting.modes) {
                                if (!settingObject.has(s))
                                    continue;

                                if (settingObject.get(s).getAsBoolean())
                                    multiSetting.toggle(s);
                            }
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    case NumberSetting numberSetting -> {
                        if (!settingObject.has("value"))
                            return;

                        try {
                            numberSetting.setValue(settingObject.get("value").getAsFloat());
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    case TextInputSetting textInputSetting -> {
                        if (!settingObject.has("text"))
                            return;

                        try {
                            textInputSetting.setValue(settingObject.get("text").getAsString());
                        } catch (Exception e) {
                            Argon.getInstance().logger.error("Failed to set setting!", e);
                        }
                    }

                    default -> {
                        Argon.getInstance().logger.error("Cannot load setting type of {}", setting.getDisplayName());
                    }
                }
            });
        });
    }
}
