import java.util.Random;

/**
 * A class representing the characteristics of a weather system. The weather system sets the weather for random but predetermined periods of time.
 *
 * @author Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class WeatherSystem
{
    // Random number generator.
    private Random random = new Random();
    // The current weather.
    private weatherTypes currentWeather;
    // The the number of steps before the weather changes. Between 10-15.
    private int weatherCooldown;
    // The current cooldown.
    private int currentCooldown;

    public enum weatherTypes {
        CLEAR_SKIES, RAIN, FOG, THUNDER_STORM;

        public static final int ENUM_COUNT = weatherTypes.values().length;
        private static final int THUNDER_INTENSITY = 5;
        private static int killedThunder = 0;

        public static int getThunderIntensity()
        {
            return THUNDER_INTENSITY;
        }
    }

    /**
     * Creates a new weather system and sets the initial weather and cooldown.
     */
    public WeatherSystem()
    {
        currentCooldown = 0;
        changeWeather();
    }

    /**
     * Attempts to change the weather. If the cooldown has elapsed, the weather will be changed randomly.
     */
    public void changeWeather()
    {
        if (currentCooldown == 0) {
            currentWeather = weatherTypes.values()[random.nextInt(weatherTypes.ENUM_COUNT)];
            weatherCooldown = 10 + random.nextInt(6);
            currentCooldown = weatherCooldown;
        }
        else {
            currentCooldown--;
        }
    }

    /**
     * @return  A String displaying the weather information
     */
    public String createWeatherString()
    {
        return " - Weather: {" + currentWeather + "}";
    }

    /**
     * @return  True if the weather is clear skies, false otherwise.
     */
    public boolean isClearSkies()
    {
        if (currentWeather == weatherTypes.CLEAR_SKIES) {
            return true;
        }
        return false;
    }

    /**
     * @return  True if the weather is rain, false otherwise.
     */
    public boolean isRaining()
    {
        if (currentWeather == weatherTypes.RAIN) {
            return true;
        }
        return false;
    }

    /**
     * @return  True if the weather is fog, false otherwise.
     */
    public boolean isFog()
    {
        if (currentWeather == weatherTypes.FOG) {
            return true;
        }
        return false;
    }

    /**
     * @return  True if the weather is thunder storm, false otherwise.
     */
    public boolean isThunderStorm()
    {
        if (currentWeather == weatherTypes.THUNDER_STORM) {
            return true;
        }
        return false;
    }

    public static int getKilledThunder()
    {
        return weatherTypes.killedThunder;
    }
    
    public static void killByThunder()
    {
        weatherTypes.killedThunder++;
    }
}
