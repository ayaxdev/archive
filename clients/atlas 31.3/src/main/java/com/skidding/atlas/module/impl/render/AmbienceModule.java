package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.event.impl.world.weather.OverrideSnowEvent;
import com.skidding.atlas.event.impl.game.RunTickEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.Random;

public final class AmbienceModule extends ModuleFeature {

    public final SettingFeature<Boolean> changeTime = check("Change time", true).build();
    public final SettingFeature<Float> time = slider("Time", 12000, 0, 24000, 0)
            .addDependency(changeTime).build();

    public final SettingFeature<Boolean> changeWeather = check("Change weather", false).build();
    public final SettingFeature<String> weather = mode("Weather", "Clear", new String[]{"Clear", "Rain", "Thunder", "Snow"})
            .addDependency(changeWeather).build();


    //A random value was used in the clear weather command, so here's one to use
    private final int randomValue = (300 + (new Random()).nextInt(600)) * 20;


    public AmbienceModule() {
        super(new ModuleBuilder("Ambience", "Allows you to modify the game time and weather", ModuleCategory.RENDER));
    }

    @EventHandler
    public void onTick(RunTickEvent runTickEvent) {
        if (getWorld() != null) {

            WorldInfo worldinfo = getWorld().getWorldInfo();
            if (mc.isSingleplayer()) {
                World world = MinecraftServer.getServer().worldServers[0];
                worldinfo = world.getWorldInfo();
            }

            if (changeTime.getValue())
                getWorld().setWorldTime(time.getValue().longValue());

            if (changeWeather.getValue()) {
                switch (weather.getValue()) {
                    case "Clear" -> {
                        worldinfo.setCleanWeatherTime(randomValue);
                        worldinfo.setRainTime(0);
                        worldinfo.setThunderTime(0);
                        worldinfo.setRaining(false);
                        worldinfo.setThundering(false);
                    }
                    case "Rain" -> {
                        worldinfo.setRainTime(Integer.MAX_VALUE);
                        worldinfo.setThunderTime(Integer.MAX_VALUE);
                        worldinfo.setRaining(true);
                        worldinfo.setThundering(false);
                    }
                    case "Thunder" -> {
                        worldinfo.setCleanWeatherTime(0);
                        worldinfo.setRainTime(Integer.MAX_VALUE);
                        worldinfo.setThunderTime(Integer.MAX_VALUE);
                        worldinfo.setRaining(true);
                        worldinfo.setThundering(true);
                    }
                    case "Snow" -> {
                        worldinfo.setCleanWeatherTime(0);
                        worldinfo.setRainTime(Integer.MAX_VALUE);
                        worldinfo.setThunderTime(Integer.MAX_VALUE);
                        worldinfo.setRaining(true);
                        worldinfo.setThundering(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onOverrideSnow(OverrideSnowEvent overrideSnowEvent) {
        if (changeWeather.getValue())
            overrideSnowEvent.shouldOverride = weather.getValue().equals("Snow");
    }

    @EventHandler
    public void onPacket(HandlePacketEvent handlePacketEvent) {
        if (changeTime.getValue() && handlePacketEvent.packet instanceof S03PacketTimeUpdate) {
            handlePacketEvent.cancelled = true;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
