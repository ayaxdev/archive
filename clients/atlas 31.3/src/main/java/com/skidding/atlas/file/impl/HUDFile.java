package com.skidding.atlas.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skidding.atlas.file.FileFeature;
import com.skidding.atlas.hud.HUDFactory;
import com.skidding.atlas.hud.HUDElement;
import com.skidding.atlas.hud.HUDManager;
import com.skidding.atlas.hud.impl.TextElement;
import com.skidding.atlas.hud.util.Side;
import com.skidding.atlas.util.encryption.EncryptionUtil;
import com.skidding.atlas.util.java.object.EnumUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HUDFile extends FileFeature {

    public HUDFile() {
        super("hud", EncryptionUtil.KeyLevel.LOW);
    }

    @Override
    public void load(Gson gson) {
        if(!this.getSavedFile().exists()) {
            try(FileWriter fileWriter = new FileWriter(getSavedFile())) {
                // TODO: add a proper way to set default hud
                fileWriter.write("""
                        {
                          "hud": {
                            "ModuleList:2312": {
                              "X": 100.0,
                              "Y": 1.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Right",
                              "Vertical facing": "Up"
                            },
                            "Text:66390": {
                              "X": 95.0,
                              "Y": 512.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Middle left",
                              "Vertical facing": "Up"
                            },
                            "Text:32139": {
                              "X": 478.0,
                              "Y": 270.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            },
                            "Text:16617": {
                              "X": 2.0,
                              "Y": 19.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            },
                            "Text:91747": {
                              "X": 2.0,
                              "Y": 31.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            },
                            "Text:WatermarkTextElement": {
                              "X": 3.0,
                              "Y": 2.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            },
                            "Text:11083": {
                              "X": 41.0,
                              "Y": 2.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            },
                            "Text:96642": {
                              "X": 2.0,
                              "Y": 43.0,
                              "Scale": 1.0,
                              "Priority": 0,
                              "Horizontal facing": "Left",
                              "Vertical facing": "Up"
                            }
                          }
                        }""");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        super.load(gson);
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();

        for(HUDElement hudElement : HUDManager.getSingleton().renderElements) {
            jsonObject.add(hudElement.getName(), hudElement.serialize());
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final String name = entry.getKey();
            final JsonObject elementObject = entry.getValue().getAsJsonObject();

            final HUDFactory hudFactory = HUDManager.getSingleton().getByName(name.split(":")[0]);

            if(hudFactory != null && elementObject != null) {
                final float x = elementObject.get("X").getAsFloat(),
                        y = elementObject.get("Y").getAsFloat();
                final float scale = elementObject.get("Scale").getAsFloat();
                final int priority = elementObject.get("Priority").getAsInt();
                final Side.Horizontal horizontal = EnumUtil.getEnumConstantBasedOnString(Side.Horizontal.class, elementObject.get("Horizontal facing").getAsString());
                final Side.Vertical vertical = EnumUtil.getEnumConstantBasedOnString(Side.Vertical.class, elementObject.get("Vertical facing").getAsString());

                final HUDElement hudElement = hudFactory.build(name, x, y, priority, new Side(horizontal, vertical));
                hudElement.scale = scale;
                HUDManager.getSingleton().add(hudElement);
            }
        }
    }

}
