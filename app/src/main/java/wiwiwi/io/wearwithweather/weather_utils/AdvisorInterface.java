package wiwiwi.io.wearwithweather.weather_utils;

/**
 * Created by dilkom-hak on 18.05.2016.
 */
public interface AdvisorInterface {
    Advisor getRainResult(String gender, Double temp, String description);

    Advisor getThunderstormResult(String gender, Double temp, String description);

    Advisor getDrizzleResult(String gender, Double temp, String description);

    Advisor getSnowResult(String gender, Double temp, String description);

    Advisor getAtmosphereResult(String gender, Double temp, String description);

    Advisor getClearResult(String gender, Double temp, String description);

    Advisor getCloudsResult(String gender, Double temp, String description);

}