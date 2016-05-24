package wiwiwi.io.wearwithweather.weather_utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dilkom-hak on 18.05.2016.
 */
public class Weather_Wizard {

    private static final String TAG = "Weather";
    Advisor my_Advisor;

    /*CLEAR TYPES */
    public static String CLEAR_SKY = "clear sky";

    /*RAIN TYPES */
    public static String LIGHT_RAIN = "light rain";
    public static String MODERATE_RAIN = "moderate rain";
    public static String HEAVY_INTENSITY_RAIN = "heavy intensity rain";
    public static String VERY_HEAVY_RAIN = "very heavy rain";
    public static String EXTREME_RAIN = "extreme rain";
    public static String FREEZING_RAIN = "freezing rain";
    public static String LIGHT_INTENSITY_SHOWER_RAIN = "light intensity shower rain";
    public static String SHOWER_RAIN = "shower rain";
    public static String HEAVY_INTENSITY_SHOWER_RAIN = "heavy intensity shower rain";
    public static String RAGGED_SHOWER_RAIN = "ragged shower rain";

    /* CLOUDS TYPES */
    public static String FEW_CLOUDS = "few clouds";
    public static String SCATTERED_CLOUDS = "scattered clouds";
    public static String BROKEN_CLOUDS = "broken clouds";
    public static String OVERCAST_CLOUDS = "overcast clouds";

    /* SNOW TYPES */
    public static String LIGHT_SNOW = "light snow";
    public static String SNOW = "snow";
    public static String HEAVY_SNOW = "heavy snow";
    public static String SLEET = "sleet";
    public static String SHOWER_SLEET = "shower sleet";
    public static String LIGHT_RAIN_AND_SNOW = "light rain and snow";
    public static String RAIN_AND_SNOW = "rain and snow";
    public static String SHOWER_SNOW = "shower snow";
    public static String HEAVY_SHOWER_SNOW = "heavy shower snow";

    /*THUNDERSTORM TYPES */
    public static String THUNDERSTORM_WITH_LIGHT_RAIN = "thunderstorm with light rain";
    public static String THUNDERSTORM_WITH_RAIN = "thunderstorm with rain";
    public static String THUNDERSTORM_WITH_HEAVY_RAIN = "thunderstorm with heavy rain";
    public static String LIGHT_THUNDERSTORM = "light thunderstorm";
    public static String THUNDERSTORM = "thunderstorm";
    public static String HEAVY_THUNDERSTORM = "heavy thunderstorm";
    public static String RAGGED_THUNDERSTORM = "ragged thunderstorm";
    public static String THUNDERSTORM_WITH_LIGHT_DRIZZLE = "thunderstorm with light drizzle";
    public static String THUNDERSTORM_WITH_DRIZZLE = "thunderstorm with drizzle";
    public static String THUNDERSTORM_WITH_HEAVY_DRIZZLE = "thunderstorm with heavy drizzle";

    String userGender;
    Double temperature;
    String main;
    String description;

  public Weather_Wizard(String userGender, Double temperature, String main, String description) {
        this.userGender = userGender;
        this.temperature = temperature;
        this.main = main;
        this.description = description;

    }


    public Advisor Clothes_Advisor(String main) {
        AdvisorMethods advisorMethods = new AdvisorMethods();
        my_Advisor = new Advisor();
        Log.d(TAG, "main=" + main);
        switch (main) {
            case WeatherTypes.RAIN:
                Log.d(TAG,"rain");
                my_Advisor = advisorMethods.getRainResult(userGender, temperature, description);
                break;

            case WeatherTypes.THUNDERSTORM:
                Log.d(TAG,"rain");

                my_Advisor = advisorMethods.getThunderstormResult(userGender, temperature, description);

                break;
            case WeatherTypes.ATMOSPHERE:
                my_Advisor = advisorMethods.getAtmosphereResult(userGender, temperature, description);

                break;
            case WeatherTypes.DRIZZLE:
                my_Advisor = advisorMethods.getDrizzleResult(userGender, temperature, description);

                break;
            case WeatherTypes.CLOUDS:
                Log.d(TAG,"clouds");

                my_Advisor = advisorMethods.getCloudsResult(userGender, temperature, description);

                break;
            case WeatherTypes.SNOW:
                my_Advisor = advisorMethods.getSnowResult(userGender, temperature, description);

                break;
            case WeatherTypes.CLEAR:
                Log.d(TAG,"clear");

                my_Advisor = advisorMethods.getClearResult(userGender, temperature, description);

                break;
            default:
                Log.d(TAG,"default");
                break;
        }


        return my_Advisor;
    }


    class AdvisorMethods implements AdvisorInterface {
        private Advisor advisor;
        private ArrayList<String> clothesList = new ArrayList<>();
        private String comment = "";

        public AdvisorMethods() {
            advisor = new Advisor();
        }

        @Override
        public Advisor getRainResult(String gender, Double temp, String desc) {
            comment = "Yağmurlu bir hava bizi bekliyor..\n";

            //14 derece light rain
            //12
            clothesList.clear();
            if(desc.equals(LIGHT_RAIN) || desc.equals(MODERATE_RAIN) || desc.equals(LIGHT_INTENSITY_SHOWER_RAIN))
            {
                if(temp <= 10)
                {
                    comment += "Hafif bir yağmurla beraber dışarısı soğuk görünüyor. Güzel giyin derim";
                    clothesList.add(ClothesTypes.INCE_CEKET);
                    clothesList.add(ClothesTypes.INCE_MONT);
                }
                else if(temp > 10 && temp <= 18)
                {
                    comment += "Kıyafetlerimize biraz dikkat etme vakti. Serin olucak";
                    clothesList.add(ClothesTypes.INCE_CEKET);
                    clothesList.add(ClothesTypes.INCE_MONT);

                }
                else if(temp > 18)
                {
                    clothesList.add(ClothesTypes.SWEATSHIRT);
                    clothesList.add(ClothesTypes.HIRKA);
                    comment += "Yaz yağmuru desek yeridir. İnce bir hırka hiiç fena olmaz ";
                }

            }
            else if(desc.equals(HEAVY_INTENSITY_RAIN) || desc.equals(VERY_HEAVY_RAIN) || desc.equals(EXTREME_RAIN) || desc.equals(SHOWER_RAIN))
            {
                if(temp <= 10)
                {
                    comment += "Şiddetli bir yağmurla beraber dışarısı soğuk görünüyor. Güzel giyin derim";
                    clothesList.add(ClothesTypes.INCE_MONT);
                }
                else if(temp > 10 && temp <= 18)
                {
                    comment += "Kıyafetlerimize biraz dikkat etme vakti. Serin olucak";
                    clothesList.add(ClothesTypes.INCE_CEKET);
                    clothesList.add(ClothesTypes.INCE_MONT);

                }
                else if(temp > 18)
                {
                    clothesList.add(ClothesTypes.SWEATSHIRT);
                    clothesList.add(ClothesTypes.HIRKA);
                    comment += "Yaz yağmuru desek yeridir. İnce bir hırka hiiç fena olmaz ";
                }
            }
            else if(desc.equals(RAGGED_SHOWER_RAIN) || desc.equals(HEAVY_INTENSITY_SHOWER_RAIN) || desc.equals(FREEZING_RAIN))
            {
                comment += "Ben olsam çık abi dışarı. \n Pencere kenarından kahve keyfi misss";
                clothesList.add(ClothesTypes.KAHVELI_VE_YAGMURLU);
            }

            advisor.setComment(comment);
            advisor.setClothesList(clothesList);

            return advisor;
        }

        @Override
        public Advisor getThunderstormResult(String gender, Double temp, String description) {
            return null;
        }

        @Override
        public Advisor getDrizzleResult(String gender, Double temp, String description) {
            return null;
        }

        @Override
        public Advisor getSnowResult(String gender, Double temp, String description) {
            clothesList.clear();

            Advisor localAdvisor = new Advisor();
            ArrayList<String> localList = new ArrayList<>();
            comment = "Winter has already comed.";
            comment += "Gebereceenn gebereceen. Mont giy mk!";

            clothesList.add(ClothesTypes.INCE_CEKET);
            clothesList.add(ClothesTypes.INCE_MONT);

            localAdvisor.setClothesList(clothesList);
            localAdvisor.setComment(comment);
            return localAdvisor;
        }

        @Override
        public Advisor getAtmosphereResult(String gender, Double temp, String description) {
            return null;
        }

        @Override
        public Advisor getClearResult(String gender, Double temp, String desc) {

            Advisor localAdvisor = new Advisor();
            ArrayList<String> localList = new ArrayList<>();
            Log.d(TAG,"GELEN PARAMETRELER.. gender = " + gender + ", temp:" +temp + ",decritpion = "+ desc );


            comment = "Güneşli bir hava bizi bekliyor..\n";
            Log.d(TAG,"getClearResult Start");
            clothesList.clear();
            if(desc.equals(CLEAR_SKY))
            {
                Log.d(TAG,"decs=CLEAR_SKY ı geçtik");


                if(temp < 10)
                {
                    Log.d(TAG,"temp < 10 dan...");
                    clothesList.add(ClothesTypes.INCE_CEKET);
                    clothesList.add(ClothesTypes.HIRKA);
                    comment += "Ama çokta güneşe aldanma!";

                }
                else if(temp >= 10 && temp <= 16)
                {
                    if(gender == "Male")
                    {
                        clothesList.add(ClothesTypes.SWEATSHIRT);
                        clothesList.add(ClothesTypes.HIRKA);
                        clothesList.add(ClothesTypes.GOMLEK);

                    }
                    else if(gender == "Female")
                    {
                        clothesList.add(ClothesTypes.SWEATSHIRT);
                        clothesList.add(ClothesTypes.TUNIK);
                        clothesList.add(ClothesTypes.GOMLEK);

                    }
                    comment += "Yavaş yavaş ısınıyoooruz.";

                }
                else if(temp > 16 && temp <= 21)
                {
                    if(gender == "Male")
                    {
                        clothesList.add(ClothesTypes.SWEATSHIRT);
                        clothesList.add(ClothesTypes.TISORT);
                        clothesList.add(ClothesTypes.GOMLEK);

                    }
                    else if(gender == "Female")
                    {
                        clothesList.add(ClothesTypes.GOMLEK);
                        clothesList.add(ClothesTypes.BLUZ);
                        clothesList.add(ClothesTypes.ELBISE);

                    }
                    comment += "Bahar havası, gevşer gönül yayları..";

                }
                else if(temp >= 22 && temp <= 26)
                {
                    if(gender == "Male")
                    {
                        clothesList.add(ClothesTypes.SWEATSHIRT);
                        clothesList.add(ClothesTypes.TISORT);
                        clothesList.add(ClothesTypes.SORT);
                    }
                    else if(gender == "Female")
                    {
                        clothesList.add(ClothesTypes.TISORT);
                        clothesList.add(ClothesTypes.BLUZ);
                        clothesList.add(ClothesTypes.ELBISE);

                    }
                    comment += "Sıcak çok sıcak, sıcak daha da sıcak olucaaak";

                }
                else // > 30
                {
                    if(gender == "Male")
                    {
                        clothesList.add(ClothesTypes.TISORT);
                        clothesList.add(ClothesTypes.SORT);

                    }
                    else if(gender == "Female")
                    {
                        clothesList.add(ClothesTypes.ELBISE);
                        clothesList.add(ClothesTypes.ETEK);
                        clothesList.add(ClothesTypes.BLUZ);



                    }
                    comment += "Aman Allah'ım! Dışarısı yanıyoor";

                }
            }

            Log.d(TAG, "comment =>" +comment);
            Log.d(TAG, "clothes list =>" +clothesList);

            localAdvisor.setComment(comment);
            localAdvisor.setClothesList(clothesList);

            return localAdvisor;

        }

        @Override
        public Advisor getCloudsResult(String gender, Double temp, String desc) {
            comment = "Bulut mu o?\n";
            Advisor localAdvisor = new Advisor();
            ArrayList<String> localList = new ArrayList<>();
            Log.d(TAG,"CLOUDS GELEN PARAMETRELER.. gender = " + gender + ", temp:" +temp + ",decritpion = "+ desc );

            clothesList.clear();

            if(desc.equals(FEW_CLOUDS))
            {
                comment += "Az bulutlu, hatta bulutcuk :)";
                if(gender == "Male")
                {
                    clothesList.add(ClothesTypes.SORT);
                    clothesList.add(ClothesTypes.TISORT);

                }
                else
                {
                    clothesList.add(ClothesTypes.TISORT);
                    clothesList.add(ClothesTypes.ETEK);
                    clothesList.add(ClothesTypes.ELBISE);

                }
            }
            else if(desc.equals(SCATTERED_CLOUDS))
            {
                comment += "Eveet, hemde parçalı bulutlu. Gökyüzü bi başka güzel oluyor";
                if(gender == "Male")
                {
                    clothesList.add(ClothesTypes.GOMLEK);
                    clothesList.add(ClothesTypes.TISORT);

                }
                else
                {
                    clothesList.add(ClothesTypes.ELBISE);
                    clothesList.add(ClothesTypes.TISORT);
                }
            }
            else if(desc.equals(BROKEN_CLOUDS) || desc.equals(OVERCAST_CLOUDS))
            {
                comment += "omg! bulut cümbüşü vaaaar";
                if(gender == "Male")
                {
                    clothesList.add(ClothesTypes.SWEATSHIRT);
                    clothesList.add(ClothesTypes.INCE_CEKET);
                    clothesList.add(ClothesTypes.GOMLEK);
                }
                else
                {
                    clothesList.add(ClothesTypes.SWEATSHIRT);
                    clothesList.add(ClothesTypes.HIRKA);
                    clothesList.add(ClothesTypes.GOMLEK);

                }
            }


            localAdvisor.setComment(comment);
            localAdvisor.setClothesList(clothesList);

            return localAdvisor;
        }
    }

}
