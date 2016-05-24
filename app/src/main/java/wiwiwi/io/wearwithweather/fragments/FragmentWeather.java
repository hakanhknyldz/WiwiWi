package wiwiwi.io.wearwithweather.fragments;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.adapters.WeatherAdapter;
import wiwiwi.io.wearwithweather.network.Controller;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.network.weatherMethods;
import wiwiwi.io.wearwithweather.pojo.WeatherResult;
import wiwiwi.io.wearwithweather.pojo.wiClothes;
import wiwiwi.io.wearwithweather.weather_utils.Advisor;
import wiwiwi.io.wearwithweather.weather_utils.Weather_Wizard;


public class FragmentWeather extends Fragment {
    VolleyApplication volleyApplication;
    public static String apiUrl = "http://api.openweathermap.org/data/2.5";
    public static String apiKey = "4306caa738903a7a8223e04bfe6bacc6";
    Boolean firstUse = true;
    String url = "";
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    ArrayList<WeatherResult> weatherArray = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "currentLocation";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HAKKE";
    Button btnQrReader;
    // TODO: Rename and change types of parameters
    private String currentLocation;
    private String userGender;
    RecyclerView recyclerView;
    WeatherAdapter weatherAdapter;
    WeatherResult currentWeather;
    ArrayList<WeatherResult> forecastWeather;
    /* these arrays keep clothes objects coming from wiService */
    ArrayList<ArrayList<wiClothes>> wiClothesArrayList = new ArrayList<>();
    private ProgressDialog progressDialog = null;
    public static FragmentWeather newInstance(String param1, String param2) {
        FragmentWeather fragment = new FragmentWeather();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWeather() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"FragmentWeather=> onCreate");
        if (getArguments() != null) {
            currentLocation = getArguments().getString(ARG_PARAM1);
            userGender = getArguments().getString(ARG_PARAM2);

            Log.d(TAG,"FragmentWeather=> onCreate=> curr Locat:" +currentLocation +" , uGender:" + userGender );

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_weather, container, false);
        volleyApplication = VolleyApplication.getInstance();

        weatherMethods weatherMethods = new weatherMethods(userGender);
        Log.d(TAG,"currentLocation => " + currentLocation);



        forecastWeather = new ArrayList<WeatherResult>();
        currentWeather  = new WeatherResult();

        String arr[] = currentLocation.split(",");
        final double lat = Double.parseDouble(arr[0]);
        final double lon = Double.parseDouble(arr[1]);
        Log.d(TAG, "lat:" + lat + ",lon:" + lon);

        volleyApplication.init(getActivity());

        //EVET BAŞLIYORUZ.
        //ÖNCELİKLE PROGRESSDIALOG KOYALIM BİR TANE
       // progressDialog = new ProgressDialog(this.getActivity(),ProgressDialog.STYLE_SPINNER);
       // progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getContext(), "Öneriler Getiriliyor","Öneriler Getiriliyor", false, true);


        //RECYCLERVIEW EKLENDI.
        //GRIDLAYOUT KULLANACAGIZ RECYCLERVIEW IN
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        //weatherAdapter = new WeatherAdapter(getContext(),resultArray);
        recyclerView.setAdapter(null); //Şimdilik adapter 'ümüz null. En son işlemden sonra adapter eklenecek..
        //weatherAdapter.notifyDataSetChanged();
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2); // (Context context, int spanCount)
        recyclerView.setLayoutManager(mGridLayoutManager);

        //İlk request çağırılır.. bu request currentWeather için
        WeatherResult current = new WeatherResult();
        String query = "/weather?lat="+lat+"&lon="+lon;
        if (!apiKey.isEmpty()) {
            url = apiUrl + query + "&units=metric&APPID=" + apiKey;
        }
        String firstURL = url;
        StringRequest firstRequest = new StringRequest(Request.Method.POST, firstURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /////////////////////////parseJSONforCurrentWeather();
                        //Toast.makeText(getContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, response);
                        Log.d(TAG,">>> FIRST REQUEST STARTS");
                       // Toast.makeText(getContext(),"First Request Start!",Toast.LENGTH_SHORT);


                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);

                            Log.d(TAG,"obj =>  "+ obj.toString());
                            JSONArray weatherArray = obj.getJSONArray("weather");
                            for (int i = 0; i < weatherArray.length(); i++) {
                                JSONObject currentWeatherObject = weatherArray.getJSONObject(i);
                                String id = currentWeatherObject.getString("id");
                                String main = currentWeatherObject.getString("main");
                                String description = currentWeatherObject.getString("description");
                                String weatherIconId = currentWeatherObject.getString("icon");

                                Date date = new Date();
                                currentWeather.setTitle(main);
                                currentWeather.setDescription(description);
                                currentWeather.setWeatherIconId(weatherIconId);

                                //  current.setIcon(icon);
                                currentWeather.setDate(date);

                            }

                            JSONObject mainObject = obj.getJSONObject("main");
                            double temp = mainObject.getDouble("temp");
                            double pressure = mainObject.getDouble("pressure");
                            double humidity = mainObject.getDouble("humidity");
                            double temp_max = mainObject.getDouble("temp_max");
                            double temp_min = mainObject.getDouble("temp_min");

                            currentWeather.setTemp(temp);
                            currentWeather.setPressure(pressure);
                            currentWeather.setHumidity(humidity);


                            Log.d(TAG,"getCurrentWeatherByCoor => currentWeather Object =>" + currentWeather);
                            Log.d(TAG,"getCurrentWeatherByCoor => CurrentWeather Object getTemp =>" + currentWeather.getTemp());
                        }
                        catch (JSONException e) {
                            Log.d(TAG,"CurrentWeather error =>" + e);
                            e.printStackTrace();

                        }

                        //onResponse olduğunda artık ikinci request çağırılacak.
                        //bu requestimiz forecastWeather için
                        final ArrayList<WeatherResult> forecastArray = new ArrayList<WeatherResult>();

                        String query = "/forecast?lat="+lat+"&lon="+lon;
                        if (!apiKey.isEmpty()) {
                            url = apiUrl + query + "&cnt=4&units=metric&APPID=" + apiKey;
                        }
                        String secondUrl = url;
                        StringRequest secondRequest = new StringRequest(Request.Method.POST, secondUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //parseJSONforForecastWeather();
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(response);
                                    Log.d(TAG,">>>>> SECOND REQUEST STARTS");

                                    Log.d(TAG, "getForecastWeatherByCoordinate => onResponse => forecast Object" + obj);

                                    JSONObject city = obj.getJSONObject("city");
                                    long id = city.getLong("id");
                                    String cityName = city.getString("name");

                                    int cnt = obj.getInt("cnt");

                                    //"list" isminden bir array oluştur
                                    JSONArray listArray = obj.getJSONArray("list");
                                    //cnt => count 4 bizim için ilkine bakmıyoruz. kalan 3 ünü alıp arraylere atacağız..
                                    //array in boyutu kadar döngü kurulur.
                                    for (int i = 1; i < listArray.length(); i++) {
                                        //////////////////////////////////////////////////////////////
                                        //arrayin ilk elemanı bir obje oldugu için listObject yaratılır.
                                        JSONObject listObject = listArray.getJSONObject(i);

                                        WeatherResult curr = new WeatherResult();

                                        //listObject in içinde "main" objesi bulunmakta..
                                        //mainObject yaratılır.
                                        JSONObject mainObject = listObject.getJSONObject("main");

                                        //mainObjesinin içinde temp, pressure, humidity
                                        //attributeleri bulunmaktadır.
                                        //onlara erişilip weather değerini tutan currentWeather mize atılırlar..
                                        curr.setTemp(mainObject.getDouble("temp"));
                                        curr.setHumidity(mainObject.getDouble("humidity"));
                                        curr.setPressure(mainObject.getDouble("pressure"));


                                        //listObjemizin içinde weather isimli array bulunmakta.
                                        JSONArray weatherArray = listObject.getJSONArray("weather");
                                        for (int j = 0; j < weatherArray.length(); j++) {
                                            //weather array i aslında tek elemanlı ama yinede array deki her eleman da birer
                                            //obje olarak gösterildiği için weatherObject yaratılır.

                                            JSONObject weatherObject = weatherArray.getJSONObject(j);

                                            //weatherObjesinin içinden Title,description,Icon attributeleri weatherObjemize atılır.
                                            curr.setTitle(weatherObject.getString("main"));
                                            curr.setDescription(weatherObject.getString("description"));
                                            curr.setWeatherIconId(weatherObject.getString("icon"));

                                        }//inner for

                                        //listArray in her elemanın en sonunda dt_txt isimli attribute bulunur..
                                        String dt_txt = listObject.getString("dt_txt");

                                        Date date = dateFormat.parse(dt_txt);
                                        //string olarak gelen zamanımızı, en üst belirlediğimiz formata göre alıp
                                        //date isimli objeye pasladık.
                                        curr.setDate(date);
                                        //weather objemize date i aktardık.
                                        //list array inde her date bulunduğumuz zmanadan 3 saat sonrasını göstermektedir..
                                        curr.setCityName(cityName);

                                        Log.d(TAG,"getForecastWeatherByCoordinate => onResponse => Object Details => " + curr.getTitle() + "," + curr.getTemp() + "," + curr.getDescription());

                                        forecastArray.add(curr);
                                    }//outer - for

                                } catch (ParseException e) {
                                    Log.d(TAG,"getForecastWeatherByCoordinate => ParseException:" +e.toString());
                                    e.printStackTrace();
                                } catch (JSONException ex) {
                                    Log.d(TAG,"getForecastWeatherByCoordinate => JSONException:" +ex.toString());
                                    ex.printStackTrace();
                                }


                                //forecast'ımızdanda gelen değerler yazılınca simdi için algoritma kısmına gelelim..
                                //yeni arraylist oluştur. ilk elemanı current olsun. kalan elemanları forecastWeatherdan kopyala..
                                final ArrayList<WeatherResult> resultArray = new ArrayList<WeatherResult>();
                                resultArray.add(currentWeather);
                                //resultArrayın ilk elemanı current Weather ımız oldu..

                                for (int a = 0 ; a < forecastArray.size() ; a++ )
                                {
                                    resultArray.add(forecastArray.get(a));
                                }
                                ArrayList<String> clothesNameList = new ArrayList<>();

                                ////////
                                //FIRST PICTURE
 //11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111//
                                Double temp = resultArray.get(0).getTemp();
                                String main = resultArray.get(0).getTitle();
                                String description = resultArray.get(0).getDescription();
                                Weather_Wizard wizard = new Weather_Wizard(userGender,temp,main,description);
                                Advisor advisor = new Advisor();
                                advisor = wizard.Clothes_Advisor(main);
                                resultArray.get(0).setComment(advisor.getComment());
                                final ArrayList<String> listOfNecessaryNameofClothes = advisor.getClothesList();

                                StringRequest thirdFirstRequest = new StringRequest(Request.Method.POST,
                                        MyApplication.getGet_Clothes_Service1(),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.d(TAG,"THIRD FIRST REQUEST STARTS!");
                                                String str =  Html.fromHtml(response).toString();
                                                JSONObject obj = null;
                                                JSONArray jsonArray = null;

                                                try {
                                                    //PARSING JSON FOR FIRST PICTURE
                                                    jsonArray = new JSONArray(str);
                                                    ArrayList<wiClothes> currentArray = new ArrayList<>();
                                                    for (int j=0; j < jsonArray.length(); j++){
                                                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                                                        wiClothes wiClothes = new wiClothes();

                                                        String wiPath = jsonObject.getString("wiPath");
                                                        String wiUrl = jsonObject.getString("wiUrl");
                                                        String catId = jsonObject.getString("catId");
                                                        String genderId = jsonObject.getString("genderId");

                                                        wiClothes.setWiUrl(wiUrl);
                                                        wiClothes.setWiPath(wiPath);
                                                        wiClothes.setCatId(catId);
                                                        wiClothes.setGenderId(genderId);

                                                        currentArray.add(wiClothes);

                                                    }
                                                    Log.d(TAG, "thirdFirstRequest => wiClothesArrayList =>" + wiClothesArrayList);
                                                    wiClothesArrayList.add(currentArray);
                                                    final ArrayList<wiClothes> selectedArray = new ArrayList<>();
                                                    Log.d(TAG, "thirdFirstRequest => wiClothesArrayList.size() =>" + wiClothesArrayList.size());

                                                    //ILK IMAGE SECIMINI DIREKT YAPACAGIZ..
                                                    //currentARRAY ı kullanacagız..
                                                    int randomIndex = randomNumberGenerator(currentArray.size() - 1);
                                                    wiClothes selectedClothes = currentArray.get(randomIndex);
                                                    selectedArray.add(selectedClothes);//selected clothes added to selected ARray!!
                                                    //Details for first randomClothes
                                                    Log.d(TAG, "Random Index = " + randomIndex + "! selectedClothes Path: " + selectedClothes.getWiPath());

                                                    //adding selectedClothes to resultArrray..
                                                    resultArray.get(0).setWiClothesObject(selectedClothes);

         //2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                    //SIRADA 2. FOTOĞRAF VAR..

                                                    Double temp = resultArray.get(1).getTemp();
                                                    String main = resultArray.get(1).getTitle();
                                                    String description = resultArray.get(1).getDescription();
                                                    Weather_Wizard wizard = new Weather_Wizard(userGender,temp,main,description);
                                                    Advisor advisor = new Advisor();
                                                    advisor = wizard.Clothes_Advisor(main);
                                                    resultArray.get(1).setComment(advisor.getComment());
                                                    final ArrayList<String> listOfNecessaryNameofClothes = advisor.getClothesList();

                                                    StringRequest thirdSecondRequest = new StringRequest(Request.Method.POST,
                                                            MyApplication.getGet_Clothes_Service1(),
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    Log.d(TAG,"THIRD SECOND REQUEST STARTS!");
                                                                    String str =  Html.fromHtml(response).toString();
                                                                    JSONObject obj = null;
                                                                    JSONArray jsonArray = null;
                                                                    try {
                                                                        //PARSING JSON FOR FIRST PICTURE
                                                                        jsonArray = new JSONArray(str);
                                                                        ArrayList<wiClothes> currentArray = new ArrayList<>();
                                                                        for (int j=0; j < jsonArray.length(); j++){
                                                                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                                                                            wiClothes wiClothes = new wiClothes();

                                                                            String wiPath = jsonObject.getString("wiPath");
                                                                            String wiUrl = jsonObject.getString("wiUrl");
                                                                            String catId = jsonObject.getString("catId");
                                                                            String genderId = jsonObject.getString("genderId");

                                                                            wiClothes.setWiUrl(wiUrl);
                                                                            wiClothes.setWiPath(wiPath);
                                                                            wiClothes.setCatId(catId);
                                                                            wiClothes.setGenderId(genderId);

                                                                            currentArray.add(wiClothes);

                                                                        }
                                                                        Log.d(TAG, "thirdSecondRequest => wiClothesArrayList =>" + wiClothesArrayList);
                                                                        wiClothesArrayList.add(currentArray);

                                                                        Log.d(TAG, "thirdSecondRequest => wiClothesArrayList.size() =>" + wiClothesArrayList.size());
                                                                        wiClothes selectedItem = randomClothesSelector(currentArray,selectedArray);
                                                                        selectedArray.add(selectedItem);
                                                                        //Details for first randomClothes
                                                                        Log.d(TAG, "Second selectedClothes Path: " + selectedItem.getWiPath());
                                                                        //adding selectedClothes to resultArrray..
                                                                        resultArray.get(1).setWiClothesObject(selectedItem);

/////////333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
                                                                        //SIRADA 3. FOTOĞRAF VAR..

                                                                        Double temp = resultArray.get(2).getTemp();
                                                                        String main = resultArray.get(2).getTitle();
                                                                        String description = resultArray.get(2).getDescription();
                                                                        Weather_Wizard wizard = new Weather_Wizard(userGender,temp,main,description);
                                                                        Advisor advisor = new Advisor();
                                                                        advisor = wizard.Clothes_Advisor(main);
                                                                        resultArray.get(2).setComment(advisor.getComment());
                                                                        final ArrayList<String> listOfNecessaryNameofClothes = advisor.getClothesList();
                                                                        StringRequest thirdThirdRequest = new StringRequest(Request.Method.POST,
                                                                                MyApplication.getGet_Clothes_Service1(),
                                                                                new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {
                                                                                        Log.d(TAG,"THIRD THIRD REQUEST STARTS!");
                                                                                        String str =  Html.fromHtml(response).toString();
                                                                                        JSONObject obj = null;
                                                                                        JSONArray jsonArray = null;
                                                                                        try {
                                                                                            //PARSING JSON FOR FIRST PICTURE
                                                                                            jsonArray = new JSONArray(str);
                                                                                            ArrayList<wiClothes> currentArray = new ArrayList<>();
                                                                                            for (int j=0; j < jsonArray.length(); j++){
                                                                                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                                                                                wiClothes wiClothes = new wiClothes();

                                                                                                String wiPath = jsonObject.getString("wiPath");
                                                                                                String wiUrl = jsonObject.getString("wiUrl");
                                                                                                String catId = jsonObject.getString("catId");
                                                                                                String genderId = jsonObject.getString("genderId");

                                                                                                wiClothes.setWiUrl(wiUrl);
                                                                                                wiClothes.setWiPath(wiPath);
                                                                                                wiClothes.setCatId(catId);
                                                                                                wiClothes.setGenderId(genderId);

                                                                                                currentArray.add(wiClothes);
                                                                                            }
                                                                                            Log.d(TAG, "thirdThirdRequest => wiClothesArrayList =>" + wiClothesArrayList);
                                                                                            wiClothesArrayList.add(currentArray);

                                                                                            Log.d(TAG, "thirdThirdRequest => wiClothesArrayList.size() =>" + wiClothesArrayList.size());
                                                                                            wiClothes selectedItem = randomClothesSelector(currentArray,selectedArray);
                                                                                            selectedArray.add(selectedItem);
                                                                                            //Details for first randomClothes
                                                                                            Log.d(TAG, "Third selectedClothes Path: " + selectedItem.getWiPath());
                                                                                            //adding selectedClothes to resultArrray..
                                                                                            resultArray.get(2).setWiClothesObject(selectedItem);
///4444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
                                                                                            //SIRADA 4. FOTOĞRAF VAR..
                                                                                            Double temp = resultArray.get(3).getTemp();
                                                                                            String main = resultArray.get(3).getTitle();
                                                                                            String description = resultArray.get(3).getDescription();
                                                                                            Weather_Wizard wizard = new Weather_Wizard(userGender,temp,main,description);
                                                                                            Advisor advisor = new Advisor();
                                                                                            advisor = wizard.Clothes_Advisor(main);
                                                                                            resultArray.get(3).setComment(advisor.getComment());
                                                                                            final ArrayList<String> listOfNecessaryNameofClothes = advisor.getClothesList();
                                                                                            StringRequest thirdFourthRequest = new StringRequest(Request.Method.POST,
                                                                                                    MyApplication.getGet_Clothes_Service1(),
                                                                                                    new Response.Listener<String>() {
                                                                                                        @Override
                                                                                                        public void onResponse(String response) {
                                                                                                            progressDialog.dismiss();


                                                                                                            Log.d(TAG,"THIRD FOURTH REQUEST STARTS!");
                                                                                                            String str =  Html.fromHtml(response).toString();
                                                                                                            JSONObject obj = null;
                                                                                                            JSONArray jsonArray = null;
                                                                                                            try {
                                                                                                                //PARSING JSON FOR FIRST PICTURE
                                                                                                                jsonArray = new JSONArray(str);
                                                                                                                ArrayList<wiClothes> currentArray = new ArrayList<>();
                                                                                                                for (int j=0; j < jsonArray.length(); j++){
                                                                                                                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                                                                                                                    wiClothes wiClothes = new wiClothes();

                                                                                                                    String wiPath = jsonObject.getString("wiPath");
                                                                                                                    String wiUrl = jsonObject.getString("wiUrl");
                                                                                                                    String catId = jsonObject.getString("catId");
                                                                                                                    String genderId = jsonObject.getString("genderId");

                                                                                                                    wiClothes.setWiUrl(wiUrl);
                                                                                                                    wiClothes.setWiPath(wiPath);
                                                                                                                    wiClothes.setCatId(catId);
                                                                                                                    wiClothes.setGenderId(genderId);

                                                                                                                    currentArray.add(wiClothes);
                                                                                                                }
                                                                                                                Log.d(TAG, "thirdFourthRequest => wiClothesArrayList =>" + wiClothesArrayList);
                                                                                                                wiClothesArrayList.add(currentArray);

                                                                                                                Log.d(TAG, "thirdFourthRequest => wiClothesArrayList.size() =>" + wiClothesArrayList.size());
                                                                                                                wiClothes selectedItem = randomClothesSelector(currentArray,selectedArray);
                                                                                                                selectedArray.add(selectedItem);
                                                                                                                //Details for first randomClothes
                                                                                                                Log.d(TAG, "Fourth selectedClothes Path: " + selectedItem.getWiPath());
                                                                                                                //adding selectedClothes to resultArrray..
                                                                                                                resultArray.get(3).setWiClothesObject(selectedItem);

/////////////////////////////////////////////////////SELECTIONSS ENDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD/////////////////////////////////////////////////////////////////////////////


                                                                                                                //son kısım adapter e ekleme..
                                                                                                                weatherAdapter = new WeatherAdapter(getContext(),resultArray);
                                                                                                                recyclerView.setAdapter(weatherAdapter);



                                                                                                            } catch (JSONException e) {
                                                                                                                e.printStackTrace();
                                                                                                            }

                                                                                                        }
                                                                                                    },
                                                                                                    new Response.ErrorListener() {
                                                                                                        @Override
                                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                                            Log.d(TAG,"thirdFourthRequest => Error => " + error);

                                                                                                        }
                                                                                                    }
                                                                                            ){
                                                                                                @Override
                                                                                                protected Map<String,String> getParams() {
                                                                                                    Map<String, String> params = new HashMap<String, String>();

                                                                                                    Log.d(TAG,"mapin icindeyiz. list of size :" + listOfNecessaryNameofClothes.size());

                                                                                                    if(listOfNecessaryNameofClothes.size() == 1)
                                                                                                    {
                                                                                                        params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                                        params.put("catName2","");
                                                                                                        params.put("catName3","");
                                                                                                        params.put("genderType",userGender);

                                                                                                    }
                                                                                                    else if(listOfNecessaryNameofClothes.size() == 2){
                                                                                                        params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                                        params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                                                        params.put("catName3","");
                                                                                                        params.put("genderType",userGender);

                                                                                                    }
                                                                                                    else if(listOfNecessaryNameofClothes.size() == 3)
                                                                                                    {
                                                                                                        params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                                        params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                                                        params.put("catName3",listOfNecessaryNameofClothes.get(2));
                                                                                                        params.put("genderType",userGender);

                                                                                                    }
                                                                                                    return params;
                                                                                                }
                                                                                            };

                                                                                            volleyApplication.getRequestQueue().add(thirdFourthRequest);


                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                },
                                                                                new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                        Log.d(TAG,"thirdThirdRequest => Error => " + error);

                                                                                    }
                                                                                }
                                                                        ){
                                                                            @Override
                                                                            protected Map<String,String> getParams() {
                                                                                Map<String, String> params = new HashMap<String, String>();

                                                                                Log.d(TAG,"mapin icindeyiz. list of size :" + listOfNecessaryNameofClothes.size());

                                                                                if(listOfNecessaryNameofClothes.size() == 1)
                                                                                {
                                                                                    params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                    params.put("catName2","");
                                                                                    params.put("catName3","");
                                                                                    params.put("genderType",userGender);

                                                                                }
                                                                                else if(listOfNecessaryNameofClothes.size() == 2){
                                                                                    params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                    params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                                    params.put("catName3","");
                                                                                    params.put("genderType",userGender);

                                                                                }
                                                                                else if(listOfNecessaryNameofClothes.size() == 3)
                                                                                {
                                                                                    params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                                    params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                                    params.put("catName3",listOfNecessaryNameofClothes.get(2));
                                                                                    params.put("genderType",userGender);

                                                                                }
                                                                                return params;
                                                                            }
                                                                        };

                                                                        volleyApplication.getRequestQueue().add(thirdThirdRequest);


                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.d(TAG,"thirdSecondRequest => Error => " + error);
                                                                }
                                                            }){
                                                        @Override
                                                        protected Map<String,String> getParams() {
                                                            Map<String, String> params = new HashMap<String, String>();

                                                            Log.d(TAG,"mapin icindeyiz. list of size :" + listOfNecessaryNameofClothes.size());

                                                            if(listOfNecessaryNameofClothes.size() == 1)
                                                            {
                                                                params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                params.put("catName2","");
                                                                params.put("catName3","");
                                                                params.put("genderType",userGender);

                                                            }
                                                            else if(listOfNecessaryNameofClothes.size() == 2){
                                                                params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                params.put("catName3","");
                                                                params.put("genderType",userGender);

                                                            }
                                                            else if(listOfNecessaryNameofClothes.size() == 3)
                                                            {
                                                                params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                                                params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                                                params.put("catName3",listOfNecessaryNameofClothes.get(2));
                                                                params.put("genderType",userGender);

                                                            }
                                                            return params;
                                                        }
                                                    };

                                                    volleyApplication.getRequestQueue().add(thirdSecondRequest);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        },
                                        new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG,"thirdFirstRequest => Error => " + error);
                                    }
                                }){
                                    @Override
                                    protected Map<String,String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();

                                        Log.d(TAG,"thirdFirstRequest  mapin icindeyiz. list of size :" + listOfNecessaryNameofClothes.size());

                                        if(listOfNecessaryNameofClothes.size() == 1)
                                        {
                                            params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                            params.put("catName2","");
                                            params.put("catName3","");
                                            params.put("genderType",userGender);

                                        }
                                        else if(listOfNecessaryNameofClothes.size() == 2){
                                            params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                            params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                            params.put("catName3","");
                                            params.put("genderType",userGender);

                                        }
                                        else if(listOfNecessaryNameofClothes.size() == 3)
                                        {
                                            params.put("catName1",listOfNecessaryNameofClothes.get(0));
                                            params.put("catName2",listOfNecessaryNameofClothes.get(1));
                                            params.put("catName3",listOfNecessaryNameofClothes.get(2));
                                            params.put("genderType",userGender);

                                        }
                                        return params;
                                    }
                                };

                                volleyApplication.getRequestQueue().add(thirdFirstRequest);

                                }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        volleyApplication.getRequestQueue().add(secondRequest);

                    }
                }



    , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        volleyApplication.getRequestQueue().add(firstRequest);

    return view;
}


    public wiClothes randomClothesSelector(ArrayList<wiClothes> currentArray,ArrayList<wiClothes> selectedArray) {
        boolean bool = true;
        wiClothes candidateClothes = null;
        while(bool) {
            bool = false;
            int randomIndex = randomNumberGenerator(currentArray.size() - 1);
            candidateClothes = currentArray.get(randomIndex);
            //şuan bu clothesımız aday durumunda
            for (int i = 0 ; i < selectedArray.size();i++)
            {
                if(candidateClothes.getWiPath().equals(selectedArray.get(i).getWiPath()))
                {
                    bool = true;
                }
            }
        }
        return candidateClothes;
    }

    private int randomNumberGenerator(int max) {
        int min = 1, result = 0;
        Random rn = new Random();
        int range = max - min + 1;
        result = rn.nextInt(range) + min;
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


}
