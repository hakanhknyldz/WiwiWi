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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;

import wiwiwi.io.wearwithweather.MainActivity;
import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.pojo.UserDetails;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "HAKKE";
    EditText etName,etSurname,etEmail,etPassword;
    RadioButton rbMale,rbFemale;
    Button btnSignup;
    TextView tvLinkLogin;
    RadioGroup rgGender;
    String name,surname,password,username;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    Context context;
    public String birthDate;
    VolleyApplication volleyApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        setupWidgets();

        volleyApplication = VolleyApplication.getInstance();
        volleyApplication.init(getApplicationContext());
    }

    private void setupWidgets()
    {
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        tvLinkLogin = (TextView) findViewById(R.id.tv_LinkLogin);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        tvLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish the  registration screen and return to the Login Activity
                finish();
            }
        });
    }


    public void signup() {
        Log.d(TAG, "SignupActivity => signup is started..");
        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnSignup.setEnabled(false);

        name = etName.getText().toString();
        surname = etSurname.getText().toString();
        username = etEmail.getText().toString();
        password = etPassword.getText().toString();
        int gender = 1;
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == rbMale.getId()) {
            gender = 1;
        } else if (selectedId == rbFemale.getId()) {
            gender = 2;
        }
        final String sGender = gender+"";

        Log.d(TAG,"register parameterss :"+name+","+surname+","+username+","+password+","+sGender);

        //Registration Method here..

        StringRequest request = new StringRequest(Request.Method.POST , MyApplication.getRegister_Service_Tag(),createSuccessListener(),createErrorListener())
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("name",name);
                params.put("surname",surname);
                params.put("username",username);
                params.put("passwd",password);
                params.put("genderId",sGender);
                return params;
            }
        };
        volleyApplication.getRequestQueue().add(request);
    }

    private Response.ErrorListener createErrorListener(){
        return new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"SignUp Activity => createSuccessListener => response" + response);
                String str = Html.fromHtml(response).toString();
                Log.d(TAG,"SignUp Activity => createSuccessListener => str" + str);

                boolean result = Boolean.parseBoolean(str);
                if(result)
                {
                    Toast.makeText(context, "You registered successfully!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "SignupActivity => signup() - Signup Success!");
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
                                            UserDetails userDetailsObject = new UserDetails();

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

                                            userDetailsObject.setName(name);
                                            userDetailsObject.setSurname(surname);
                                            userDetailsObject.setPasswd(passwd);
                                            userDetailsObject.setUsername(username);
                                            userDetailsObject.setGenderType(genderType);

                                            Log.d(TAG,"LoginActivity => requestGetUserDetails getUser Name And Surname =>" + userDetailsObject.getName() + " " + userDetailsObject.getSurname() );


                                            MyApplication.saveToPreferences(context, "logged_in", true);
                                            MyApplication.saveToPreferences(context, "username", etEmail.getText().toString());

                                            Gson gson = new Gson();

                                            String userDetails = gson.toJson(userDetailsObject);

                                            MyApplication.saveToPreferences(context,"userDetails",userDetails);

                                            //MyApplication.saveToPreferences(context, "userID", result);
                                            //Log.d(TAG, "  Registered User ID is : " +  MyApplication.readFromPreferences(context,"userID", -1));
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            //MyApplication.createBundleSendingEmail(email, intent);
                                            startActivity(intent);
                                            onSignupSuccess();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG,"REGISTERACITIVITY => requestGetUserDetails Error => " + error);
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String,String>();
                            params.put("username",username);
                            return params;
                        }
                    };

                }
                else
                    Toast.makeText(context,"Email already exist!!",Toast.LENGTH_SHORT).show();
                     btnSignup.setEnabled(true);

            }
        };
    }

    public void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        btnSignup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = etName.getText().toString();
        String surname = etSurname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //validations!
        if(name.isEmpty() || name.length() < 3){
            etName.setError("at least 3 characters");
            valid = false;
        } else{
            etName.setError(null);
        }

        if(surname.isEmpty() || surname.length() < 2){
            etSurname.setError("at least 2 characters");
            valid = false;
        } else{
            etSurname.setError(null);
        }

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
}
