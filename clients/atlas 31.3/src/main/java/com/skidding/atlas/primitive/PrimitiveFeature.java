package com.skidding.atlas.primitive;

import com.google.gson.JsonObject;
import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.primitive.argument.Argument;
import com.skidding.atlas.primitive.enums.RegisterAs;
import org.apache.commons.lang3.StringUtils;

public abstract class PrimitiveFeature implements Feature {

    public final String name, description;
    public final ModuleCategory moduleCategory;
    public final Argument[] arguments;
    public RegisterAs[] registerAs = new RegisterAs[] {RegisterAs.MODULE, RegisterAs.COMMAND};

    public PrimitiveFeature(String name, String description, ModuleCategory moduleCategory, Argument... arguments) {
        this.name = name;
        this.description = description;
        this.moduleCategory = moduleCategory;
        this.arguments = arguments;
    }

    public abstract void execute(Object... args);

    public Object[] parseArgs(String[] args) {
        if (arguments.length > args.length) {
            throw new IllegalArgumentException("Incorrect amount of arguments");
        }

        Object[] parsedArgs = new Object[arguments.length];
        for (Argument argument : arguments) {
            try {
                String arg = args[argument.index];
                if (argument.type == String[].class) {
                    if (argument.index < (arguments.length - 1)) {
                        throw new IllegalArgumentException("Array argument should be last");
                    }

                    parsedArgs[argument.index] = StringUtils
                            .join(args, " ", argument.index, args.length);
                    break;
                } else if (argument.type == Integer.class || argument.type == Character.class
                        || argument.type == Byte.class) {
                    parsedArgs[argument.index] = (int) Float.parseFloat(arg);
                } else if (argument.type == Double.class) {
                    parsedArgs[argument.index] = (double) Float.parseFloat(arg);
                } else if (argument.type == Float.class) {
                    parsedArgs[argument.index] = Float.parseFloat(arg);
                } else if (argument.type == Long.class) {
                    parsedArgs[argument.index] = (long) Float.parseFloat(arg);
                } else if (argument.type == String.class) {
                    parsedArgs[argument.index] = arg;
                } else {
                    parsedArgs[argument.index] = Boolean.getBoolean(arg);
                }
            } catch (Exception e) {
                AtlasClient.getInstance().logger.error("Failed to parse arguments", e);
            }
        }
        return parsedArgs;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject serialize() { return null; }

    @Override
    public void deserialize(JsonObject jsonObject) { }

}
