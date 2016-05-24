package wiwiwi.io.wearwithweather.Registration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import wiwiwi.io.wearwithweather.MainActivity;
import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;

public class Splash extends AppCompatActivity {
    public static final String PREF_NAME = "COOKIE";
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        context = this;
        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        if(!isNetworkAvailable()){
            //Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder =  new AlertDialog.Builder(this ,R.style.AppCompatAlertDialogStyle);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setTitle("Closing the App");
                    builder.setMessage("No Internet Connection,check your settings");
                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .show();

        }
        else {
            usingAnimation(iv, an, an2);
            //usingThread();
        }

    }

    private void usingAnimation(final ImageView iv,final Animation an ,final Animation an2){
        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(an2);
                finish();
                CheckLogged();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void usingThread(){
        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                    // After 5 seconds redirect to another intent
                    CheckLogged();

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplication().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void CheckLogged(){
        //SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        Intent i;
        //if(sharedPreferences.getBoolean("logged_in",false)){ // NO LOGGED

        boolean result = MyApplication.readFromPreferences(context, "logged_in", false);

        Log.d("HAKKE", "My current logged_in SharedPreferences is : " + result);


        if(result == false)
        {
            i = new Intent(getBaseContext(),LoginActivity.class);
        }
        else{ //user already logged
            i = new Intent(getBaseContext(),MainActivity.class);
        }
        startActivity(i);
    }



}