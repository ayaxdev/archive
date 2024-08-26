package lord.daniel.alexander.util.weather;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.util.network.APIUtil;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class WeatherUtil {

    public static final String DEFAULT_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%LATITUDE%&longitude=%LONGITUDE%&current=is_day,weathercode&timezone=auto";

    public static ParsedWeather getWeather() {
        try {
            JSONObject response = APIUtil.readJsonFromUrl(DEFAULT_API_URL.
                    replace("%LATITUDE%", String.valueOf(Modification.INSTANCE.getLatitude())).
                    replace("%LONGITUDE%", String.valueOf(Modification.INSTANCE.getLongitude())));
            for(ParsedWeather parsedWeather : ParsedWeather.values()) {
                assert response != null;

                if(parsedWeather.getCode() == response.getJSONObject("current").getInt("weathercode") &&
                        (parsedWeather.getDay() == -1 || parsedWeather.getDay() == response.getJSONObject("current").getInt("is_day"))) {
                    return parsedWeather;
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static MinecraftWeather getMinecraftWeatherFromParsed(ParsedWeather parsedWeather) {
        return switch (parsedWeather) {
            case LIGHT_SNOW, SNOW, HEAVY_SNOW, SNOW_GRAINS, LIGHT_SNOW_SHOWERS, SNOW_SHOWERS -> MinecraftWeather.SNOWY;
            case LIGHT_DRIZZLE, DRIZZLE, HEAVY_DRIZZLE, LIGHT_FREEZING_DRIZZLE, FREEZING_DRIZZLE,
                    LIGHT_RAIN, RAIN, HEAVY_RAIN, LIGHT_FREEZING_RAIN, FREEZING_RAIN, LIGHT_SHOWERS, SHOWERS,
                    HEAVY_SHOWERS -> MinecraftWeather.RAINY;
            case FOGGY, RIME_FOG -> MinecraftWeather.FOGGY;
            case THUNDERSTORMS, LIGHT_THUNDERSTORMS_WITH_HAIL, THUNDERSTORM_WITH_HAIL -> MinecraftWeather.THUNDERSTORM;
            case SUNNY, CLEAR, MAINLY_SUNNY, MAINLY_CLEAR, PARTLY_CLOUDY, CLOUDY -> MinecraftWeather.CLEAR;
        };
    }

    public enum MinecraftWeather {
        SNOWY, RAINY, FOGGY, THUNDERSTORM, CLEAR;
    }

    @RequiredArgsConstructor
    @Getter
    public enum ParsedWeather {
        SUNNY("Sunny", 0, 1),
        CLEAR("Clear", 0, 0),
        MAINLY_SUNNY("Mainly Sunny", 1, 1),
        MAINLY_CLEAR("Mainly Clear", 1, 0),
        PARTLY_CLOUDY("Partly Cloudy", 2, -1),
        CLOUDY("Cloudy", 3, -1),
        FOGGY("Foggy", 45, -1),
        RIME_FOG("Rime Fog", 48, -1),
        LIGHT_DRIZZLE("Light Drizzle", 51, -1),
        DRIZZLE("Drizzle", 53, -1),
        HEAVY_DRIZZLE("Heavy Drizzle", 55, -1),
        LIGHT_FREEZING_DRIZZLE("Light Freezing Drizzle", 56, -1),
        FREEZING_DRIZZLE("Freezing Drizzle", 57, -1),
        LIGHT_RAIN("Light Rain", 61, -1),
        RAIN("Rain", 63, -1),
        HEAVY_RAIN("Heavy Rain", 65, -1),
        LIGHT_FREEZING_RAIN("Light Freezing Rain", 66, -1),
        FREEZING_RAIN("Freezing Rain", 67, -1),
        LIGHT_SNOW("Light Snow", 71, -1),
        SNOW("Snow", 73, -1),
        HEAVY_SNOW("Heavy Snow", 75, -1),
        SNOW_GRAINS("Snow Grains", 77, -1),
        LIGHT_SHOWERS("Light Showers", 80, -1),
        SHOWERS("Showers", 81, -1),
        HEAVY_SHOWERS("Heavy Showers", 82, -1),
        LIGHT_SNOW_SHOWERS("Light Snow Showers", 85, -1),
        SNOW_SHOWERS("Snow Showers", 86, -1),
        THUNDERSTORMS("Thunderstorms", 95, -1),
        LIGHT_THUNDERSTORMS_WITH_HAIL("Light Thunderstorms with Hail", 96, -1),
        THUNDERSTORM_WITH_HAIL("Thunderstorm with Hail", 99, -1);

        final String name;
        final int code, day;

        @Override
        public String toString() {
            return name;
        }

    }

}
