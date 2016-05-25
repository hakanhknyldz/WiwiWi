package wiwiwi.io.wearwithweather.Registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wiwiwi.io.wearwithweather.MainActivity;
import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.pojo.UserDetails;
import wiwiwi.io.wearwithweather.pojo.wiClothes;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "HAKKE";
    private static final int REQUEST_SIGNUP = 0;
    Context context;
    EditText etEmail,etPassword;
    Button btnLogin;
    TextView tvLinkSignup;
    VolleyApplication volleyApplication = null;
    String email = "",password = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);
        setupWidgets();
        checkLogged();

        volleyApplication = VolleyApplication.getInstance();
        volleyApplication.init(getApplicationContext());
    }

    private void setupWidgets()
    {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvLinkSignup = (TextView) findViewById(R.id.tvLinkSignup);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLinkSignup_Click();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin_Click();
            }
        });
    }

    private void checkLogged()
    {
        Intent i = null;
        boolean result = MyApplication.readFromPreferences(context, "logged_in", false);
        Log.d(TAG, "LoginActivity => checkLogged - logged_in sp = " + result);

        if(result)
        {
            //user already logged
            i = new Intent(context,MainActivity.class);
            startActivity(i);
        }
    }

    private void btnLogin_Click()
    {

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if(!validate(email,password))
        {
            onLoginFailed();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                MyApplication.getLogin_Service_Tag(),
                createSuccessListener(),
                createErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("username",email);
                params.put("passwd",password);

                return params;
            }
        };
        btnLogin.setEnabled(false);
        volleyApplication.getRequestQueue().add(request);

    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String str = Html.fromHtml(response).toString();
                boolean result = Boolean.parseBoolean(str);
                if(result)
                {
                    Toast.makeText(context,"Login Success!",Toast.LENGTH_LONG).show();
////////////GEEEET USER DETAILSSS ///////////
                    StringRequest requestGetUserDetails = new StringRequest(Request.Method.POST, MyApplication.getUser_Details_Service_Tag(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG,"LoginActiivity => requestGetUserDetails STARTS with Response :" + response);

                                    String str =  Html.fromHtml(response).toString();
                                    JSONObject obj = null;
                                    JSONArray jsonArray = null;

                                    try {
                                        jsonArray = new JSONArray(str);

                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                                            UserDetails userDetails = new UserDetails();

                                            String name = jsonObject.getString("name");
                                            String surname = jsonObject.getString("surname");
                                            String username = jsonObject.getString("username");
                                            String passwd = jsonObject.getString("passwd");
                                            int genderId = jsonObject.getInt("genderId");
                                            String genderType = "Female";
                                            if(genderId == 1)
                                            {
                                                genderType = "Male";
                                            }

                                            userDetails.setName(name);
                                            userDetails.setSurname(surname);
                                            userDetails.setPasswd(passwd);
                                            userDetails.setUsername(username);
                                            userDetails.setGenderType(genderType);

                                            Log.d(TAG,"LoginActivity => requestGetUserDetails getUser Name And Surname =>" + userDetails.getName() + " " + userDetails.getSurname() );

                                            authentication(userDetails);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG,"LoginActiivity => requestGetUserDetails Error => " + error);
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String,String>();
                            params.put("username",email);
                            return params;
                        }
                    };




                }
                else
                {
                    btnLogin.setEnabled(true);
                    Toast.makeText(context,"wrong email or password :/",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void authentication(UserDetails userDetailsObject)
    {
        //if login success
        btnLogin.setEnabled(true);
        MyApplication.saveToPreferences(context, "username", etEmail.getText().toString());
        MyApplication.saveToPreferences(context, "logged_in",true);


        //save userDetails details object to sharedPreferences..
        //we are using Gson.. it's convert object to string.. and string adding to SharedPreferences..
        //it is awesome! :D

        Gson gson = new Gson();
        String userDetails = gson.toJson(userDetailsObject);

        MyApplication.saveToPreferences(context,"userDetails",userDetails);


        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void tvLinkSignup_Click()
    {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    public boolean validate(String email,String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

}