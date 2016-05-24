package wiwiwi.io.wearwithweather.network;

import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.pojo.WeatherResult;

public class weatherMethods {

    private static final String TAG = "HAKKE";
    public static String apiUrl = "http://api.openweathermap.org/data/2.5";
    public static String apiKey = "4306caa738903a7a8223e04bfe6bacc6";
    private RequestQueue requestQueue;
    VolleyApplication volleyApplication;
    StringRequest request;
    String url = "";
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    ArrayList<WeatherResult> weatherArray = null;
    String userGender = "";
    public weatherMethods(String userGender) {
        this.userGender = userGender;
        Log.d(TAG,"weatherMethods Contructor");
        volleyApplication = VolleyApplication.getInstance();
        requestQueue = volleyApplication.getRequestQueue();
        //sendJsonRequest();
    }

    ////////////////////////////
    /* CURRENT WEATHER */
    public WeatherResult getCurrentWeatherByName(String cityName) {
        final WeatherResult[] currWeather = new WeatherResult[1];
        String query = "/weather?q=" + cityName;
        if (!apiKey.isEmpty()) {
            url = apiUrl + query + "&lang=tr&units=metric&APPID=" + apiKey;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"WeatherMethods getCurrentWeatherByName response=>" +response);
                        currWeather[0] = parseCurrentJSONResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
        return currWeather[0];
    }

    //api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}
    public WeatherResult getCurrentWeatherByCoordinate(double latitude,double longitute) {
        final WeatherResult[] currWeather = new WeatherResult[1];
        String query = "/weather?lat="+latitude+"&lon="+longitute;
        if (!apiKey.isEmpty()) {
            url = apiUrl + query + "&lang=tr&units=metric&APPID=" + apiKey;
        }
        Log.d(TAG,"weatherMethods =>getCurrentWeatherByCoordinate our url => " +url );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"WeatherMethods getCurrentWeatherByCoordinate response=>" +response);

                        currWeather[0] = parseCurrentJSONResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"getCurrentWeatherByCoordinate ERROR " + error);
            }
        });
        requestQueue.add(request);
        return currWeather[0];
    }
    private WeatherResult parseCurrentJSONResponse(JSONObject response) {
        Log.d(TAG,"parseCurrentJSONResponse => " +response);

        if (response == null || response.length() == 0) {
            return null;
        }

        WeatherResult current = new WeatherResult();

        try {
            JSONArray weatherArray = response.getJSONArray("weather");
            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject currentWeather = weatherArray.getJSONObject(i);
                String id = currentWeather.getString("id");
                String main = currentWeather.getString("main");
                String description = currentWeather.getString("description");
                String weatherIconId = currentWeather.getString("icon");

                Date date = new Date();
                current.setTitle(main);
                current.setDescription(description);
                current.setWeatherIconId(weatherIconId);

              //  current.setIcon(icon);
                current.setDate(date);

            }

            JSONObject mainObject = response.getJSONObject("main");
            double temp = mainObject.getDouble("temp");
            double pressure = mainObject.getDouble("pressure");
            double humidity = mainObject.getDouble("humidity");
            double temp_max = mainObject.getDouble("temp_max");
            double temp_min = mainObject.getDouble("temp_min");

            current.setTemp(temp);
            current.setPressure(pressure);
            current.setHumidity(humidity);
            current.setClothes_Image_Name(Resources.getSystem().getString(R.string.wi_yahoo_3200));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return current;
    }

    ////////////////////////////
    /* FORECAST */
    public ArrayList<WeatherResult> getForecastWeatherByName(String cityName) {
        weatherArray = new ArrayList<>();

        String query = "/forecast?q=" + cityName;
        if (!apiKey.isEmpty()) {
            url = apiUrl + query + "&lang=tr&cnt=3&units=metric&APPID=" + apiKey;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        weatherArray = parseForecastJSONResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(request);

        return weatherArray;
    }

    //api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}
    public ArrayList<WeatherResult> getForecastWeatherByCoordinate(double latitude,double longitude) {
        weatherArray = new ArrayList<>();

        String query = String.format("/forecast?lat={}&lon={}",latitude,longitude);

        if (!apiKey.isEmpty()) {
            url = apiUrl + query + "&lang=tr&cnt=3&units=metric&APPID=" + apiKey;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        weatherArray = parseForecastJSONResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(request);

        return weatherArray;
    }

    private ArrayList<WeatherResult> parseForecastJSONResponse(JSONObject response) {
        ArrayList<WeatherResult> forecastArray = new ArrayList<WeatherResult>();

        WeatherResult currentWeather;

        if (response == null || response.length() == 0)
            return null;

        try {
            JSONObject city = response.getJSONObject("city");
            long id = city.getLong("id");
            String cityName = city.getString("name");

            int cnt = response.getInt("cnt");

            //"list" isminden bir array oluştur
            JSONArray listArray = response.getJSONArray("list");
            //cnt => count 4 bizim için ilkine bakmıyoruz. kalan 3 ünü alıp arraylere atacağız..
            //array in boyutu kadar döngü kurulur.
            for (int i = 1; i < listArray.length(); i++) {
                //////////////////////////////////////////////////////////////
                //arrayin ilk elemanı bir obje oldugu için listObject yaratılır.
                JSONObject listObject = listArray.getJSONObject(i);

                currentWeather = new WeatherResult();

                //listObject in içinde "main" objesi bulunmakta..
                //mainObject yaratılır.
                JSONObject mainObject = listObject.getJSONObject("main");

                //mainObjesinin içinde temp, pressure, humidity
                //attributeleri bulunmaktadır.
                //onlara erişilip weather değerini tutan currentWeather mize atılırlar..
                currentWeather.setTemp(mainObject.getDouble("temp"));
                currentWeather.setHumidity(mainObject.getDouble("humidity"));
                currentWeather.setPressure(mainObject.getDouble("pressure"));


                //listObjemizin içinde weather isimli array bulunmakta.
                JSONArray weatherArray = listObject.getJSONArray("weather");
                for (int j = 0; j < weatherArray.length(); j++) {
                    //weather array i aslında tek elemanlı ama yinede array deki her eleman da birer
                    //obje olarak gösterildiği için weatherObject yaratılır.

                    JSONObject weatherObject = weatherArray.getJSONObject(j);

                    //weatherObjesinin içinden Title,description,Icon attributeleri weatherObjemize atılır.
                    currentWeather.setTitle(weatherObject.getString("main"));
                    currentWeather.setDescription(weatherObject.getString("description"));
                    currentWeather.setWeatherIconId(weatherObject.getString("icon"));
                    currentWeather.setClothes_Image_Name(Resources.getSystem().getString(R.string.wi_yahoo_3200));


                }

                //listArray in her elemanın en sonunda dt_txt isimli attribute bulunur..
                String dt_txt = listObject.getString("dt_txt");

                try {
                    Date date = dateFormat.parse(dt_txt);
                    //string olarak gelen zamanımızı, en üst belirlediğimiz formata göre alıp
                    //date isimli objeye pasladık.
                    currentWeather.setDate(date);
                    //weather objemize date i aktardık.
                    //list array inde her date bulunduğumuz zmanadan 3 saat sonrasını göstermektedir..
                    currentWeather.setCityName(cityName);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return forecastArray;
    }



}

