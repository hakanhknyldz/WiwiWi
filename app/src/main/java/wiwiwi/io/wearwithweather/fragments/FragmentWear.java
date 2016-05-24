package wiwiwi.io.wearwithweather.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.adapters.WearAdapter;
import wiwiwi.io.wearwithweather.adapters.WeatherAdapter;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.pojo.wiClothes;

public class FragmentWear extends Fragment {
    private static final String ARG_USERGENDER = "userGender";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HAKKE";
    private RecyclerView listMovieHits;
    private String userGender;
    private String mParam2;
    private ProgressDialog progressDialog = null;
    VolleyApplication volleyApplication;
    private RecyclerView recyclerView;
    WearAdapter wearAdapter;

    public static FragmentWear newInstance(String userGender, String param2) {
        FragmentWear fragment = new FragmentWear();
        Bundle args = new Bundle();
        args.putString(ARG_USERGENDER, userGender);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWear() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"FragmentWear=> onCreate");

        if (getArguments() != null) {
            userGender = getArguments().getString(ARG_USERGENDER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_wear, container, false);

        volleyApplication = VolleyApplication.getInstance();
        volleyApplication.init(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_wear);
        recyclerView.setAdapter(null);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3); // (Context context, int spanCount)
        recyclerView.setLayoutManager(mGridLayoutManager);
        String URL = MyApplication.getAll_Clothes_Service();
        Log.d(TAG,"WEAR => evet wear a basliyoruz. sirada stringrequest vaar");

        StringRequest requestForAllClothes = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"GET ALL WEARS REQUEST STARTS!");
                        String str =  Html.fromHtml(response).toString();
                        JSONObject obj = null;
                        JSONArray jsonArray = null;
                        try {
                            //PARSING JSON FOR FIRST PICTURE
                            jsonArray = new JSONArray(str);
                            ArrayList<wiClothes> currentArray = new ArrayList<>();
                            for (int j=0; j < jsonArray.length(); j++) {
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
                                Log.d(TAG,"Fragment Wear => wiClothes.wiPaths =>" + wiClothes.getWiPath());
                            }
                            Log.d(TAG,"Fragment Wear => currentArray.size() =>" + currentArray.size());



                            wearAdapter = new WearAdapter(getContext(), currentArray);
                            recyclerView.setAdapter(wearAdapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"Fragment Wear => error: " + error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("genderType",userGender);
                Log.d(TAG,"params =>" +params);
                return params;
            }
        };
        volleyApplication.getRequestQueue().add(requestForAllClothes);

        return view;
    }
}
