package com.skidding.atlas.util.text;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.combat.KillAuraModule;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.storage.TargetStorage;
import com.skidding.atlas.processor.impl.tracker.LatencyTracker;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextFormatterBuilder implements IMinecraft {
    
    private final Map<String, Supplier<Object>> placeholders = new LinkedHashMap<>();
    private final Map<String, Function<String, Object>> functions = new LinkedHashMap<>();

    private final LatencyTracker latencyTracker = ProcessorManager.getSingleton().getByClass(LatencyTracker.class);
    
    public TextFormatterBuilder addDefaultPlaceholders() {
        // Client info
        addPlaceholder("client name", () -> AtlasClient.NAME);
        addPlaceholder("client version", () -> AtlasClient.VERSION);
        addPlaceholder("client authors", () -> AtlasClient.DEVELOPERS_JOINED);
        addPlaceholder("client build", () -> AtlasClient.BUILD_NUMBER);

        // Game info
        addPlaceholder("fps", Minecraft::getDebugFPS);
        addPlaceholder("ping", () -> latencyTracker.latency);

        // Player info
        addPlaceholder("x", () -> getPlayer().posX);
        addPlaceholder("y", () -> getPlayer().posY);
        addPlaceholder("z", () -> getPlayer().posZ);
        addPlaceholder("bps", () -> MathUtil.roundAvoid(MovementUtil.INSTANCE.getBPS(), 2));
        addPlaceholder("mps", () -> MathUtil.roundAvoid(MovementUtil.INSTANCE.getBPS(), 2));
        addPlaceholder("kbph", () -> MathUtil.roundAvoid(MovementUtil.INSTANCE.getBPS() * 3.6, 2));
        addPlaceholder("kmph", () -> MathUtil.roundAvoid(MovementUtil.INSTANCE.getBPS() * 3.6, 2));

        // Session info
        addPlaceholder("name", () -> mc.session.getProfile().getName());
        addPlaceholder("uuid", () -> mc.session.getProfile().getId());

        // Server info
        addPlaceholder("remote address", () -> this.mc.getNetHandler().getNetworkManager().getRemoteAddress().toString());
        addPlaceholder("server ip", () -> this.mc.getCurrentServerData() == null ? "SinglePlayer" : this.mc.getCurrentServerData().serverIP);
        addPlaceholder("server brand", () -> getPlayer().getClientBrand().contains("<- ") ?
                STR."\{getPlayer().getClientBrand().split(" ")[0]} -> \{getPlayer().getClientBrand()
                        .split("<- ")[1]}" : getPlayer().getClientBrand().split(" ")[0]);

        // Target info
        addPlaceholder("target name", () -> TargetStorage.target == null ? "No target" : TargetStorage.target.getName());
        addPlaceholder("target health", () -> TargetStorage.target == null ? "No target" : (int) TargetStorage.target.getHealth());
        addPlaceholder("target distance", () -> TargetStorage.target == null ? "No target" : String.format("%.2f", TargetStorage.target.getDistanceToEntity(getPlayer())));

        return this;
    }

    public TextFormatterBuilder addDefaultFunctions() {
        addFunction("lower", String::toLowerCase);
        addFunction("upper", String::toUpperCase);

        return this;
    }
    
    public TextFormatterBuilder addPlaceholder(String trigger, Supplier<Object> out) {
        final String placeholderTrigger = STR."%\{trigger.toUpperCase().replace(" ", "_")}%".replace("%%", "%");
        this.placeholders.put(placeholderTrigger, out);
        
        return this;
    }
    
    public TextFormatterBuilder addFunction(String trigger, Function<String, Object> function) {
        final String functionTrigger = trigger.toLowerCase().replace(" ", "_");
        this.functions.put(functionTrigger, function);
        
        return this;
    }
    
    public TextFormatter build() {
        return new TextFormatter(placeholders, functions);
    }
    
}
