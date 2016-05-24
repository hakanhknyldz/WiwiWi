package wiwiwi.io.wearwithweather.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dilkom-hak on 23.05.2016.
 */
public class Controller {
    public Controller()
    {

    }

    public void getResultForCurrentWeather(final Context context , final String URL, final ServerCallback callback)
    {
        Log.d("HAKKE","Controller => getResultForCurrentWeather START");
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.d("HAKKE","OBAAA" + response.toString());
                callback.onSuccess(response); // call callback function here

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley error json object ", "Error: " + error.getMessage());
            }
        });

        VolleyApplication volleyApplication = VolleyApplication.getInstance();
        volleyApplication.init(context);
        volleyApplication.getRequestQueue().add(request);

    }

}
