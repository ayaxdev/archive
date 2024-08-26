package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.math.time.MinecraftTimeUtil;
import lord.daniel.alexander.util.weather.WeatherUtil;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "Ambience", enumModuleType = EnumModuleType.RENDER)
public class AmbienceModule extends AbstractModule {

    private final BooleanValue realTime = new BooleanValue("BaseOnRealTime", this, false);
    private final NumberValue<Integer> hours = new NumberValue<>("Hours", this, 19, 0, 24).addVisibleCondition(() -> !realTime.getValue());
    private final NumberValue<Integer> minutes = new NumberValue<>("Minutes", this, 0, 0, 60).addVisibleCondition(() -> !realTime.getValue());
    private final BooleanValue baseOnRealWeather = new BooleanValue("BaseOnRealWeather", this, false);
    private final StringModeValue weather = new StringModeValue("WeatherMode", this, "Clear", new String[]{"Clear", "Rain", "Snow", "Thunder"}).addVisibleCondition(() -> !baseOnRealWeather.getValue());

    @Getter
    private WeatherUtil.MinecraftWeather minecraftWeather = WeatherUtil.MinecraftWeather.CLEAR;
    private Date date;

    private DecimalFormat formatter = new DecimalFormat("00");

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        if(date == null)
            date = new Date();

        setSuffix(realTime.getValue() ? formatter.format(date.getHours()) + ":" + formatter.format(date.getMinutes()) : formatter.format(hours.getValue()) + ":" + formatter.format(minutes.getValue()));
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if(date == null)
            date = new Date();

        long time = MinecraftTimeUtil.convertToMinecraftTime(realTime.getValue() ? date.getHours() : hours.getValue(), realTime.getValue() ? date.getMinutes() : minutes.getValue());
        getWorld().setWorldTime(time);

        if(this.baseOnRealWeather.getValue()) {
            WeatherUtil.ParsedWeather parsedWeather = Modification.INSTANCE.getCurrentWeather();
            minecraftWeather = WeatherUtil.getMinecraftWeatherFromParsed(parsedWeather);
        } else {
            switch (this.weather.getValue()) {
                case "Clear" -> {
                    minecraftWeather = WeatherUtil.MinecraftWeather.CLEAR;
                }
                case "Snow" -> {
                    minecraftWeather = WeatherUtil.MinecraftWeather.SNOWY;
                }
                case "Rain" -> {
                    minecraftWeather = WeatherUtil.MinecraftWeather.RAINY;
                }
                case "Thunder" -> {
                    minecraftWeather = WeatherUtil.MinecraftWeather.THUNDERSTORM;
                }
            }
        }
    };

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if(packetEvent.getStage() == PacketEvent.Stage.RECEIVING && packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
            packetEvent.setCancelled(true);
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
