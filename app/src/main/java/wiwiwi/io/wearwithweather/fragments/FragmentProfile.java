package wiwiwi.io.wearwithweather.fragments;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

import wiwiwi.io.wearwithweather.MyApplication;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.Registration.LoginActivity;
import wiwiwi.io.wearwithweather.network.wiAlarmReceiver;

public class FragmentProfile extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HAKKE";
    Switch switch6PM,switch8AM;
    private String mParam1;
    private String mParam2;
    private Button btnLogout;
    private boolean firstUse = true;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    final int MORNING_FILTER = 1111;
    final int NIGHT_FILTER = 9999;
    final String SWITCH_8_AM ="switch8am";
    final String SWITCH_6_PM ="switch6pm";


    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "FragmentProfile => onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        setUp(view);

        //alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //Intent alarmIntent = new Intent(getContext(),wiAlarmReceiver.class);

        return view;
    }

    private void setUp(View view)
    {
        switch8AM = (Switch) view.findViewById(R.id.switch8AM);
        switch6PM = (Switch) view.findViewById(R.id.switch6PM);
        /*

        //sabah 8 => 8 oluyor. ama akşam 6 => 18 oluyoo.
*/
        boolean switch8amStatus = MyApplication.readFromPreferences(getContext(),SWITCH_8_AM,false);
        switch8AM.setChecked(switch8amStatus);

        boolean switch6pmStatus = MyApplication.readFromPreferences(getContext(),SWITCH_6_PM,false);
        switch6PM.setChecked(switch6pmStatus);

        Log.d("NOTIFICATION" , "FragmentProfile switch 8 AM => "+ switch8amStatus + " , 6 PM  => " + switch6pmStatus);
        Log.d("NOTIFICATION", "FRAGMENTPROFİLE => scheduNotification..");

        Log.d(TAG,"FirstUse start = " +firstUse);
      /*
        if(firstUse)
        {
            setNotification(8,MORNING_FILTER,"Wi wii wii! Good Morning Service","Sabah oldu. Haydi nasıl giyineceğimize karar verme vakti!");
            setNotification(18,NIGHT_FILTER , "Wi wii Wiii! Night Service","Akşama hava nasıl mı, haydi görelim :)");

            firstUse= false;

        }
        */
        Log.d(TAG,"FirstUse end = " +firstUse);
            //alarmları çalıştr..

        switch8AM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyApplication.saveToPreferences(getContext(), SWITCH_8_AM, isChecked);
                    //alarmı kur
                    setNotification(8, MORNING_FILTER, "Wi wii wii! Good Morning Service", "Sabah oldu. Haydi nasıl giyineceğimize karar verme vakti!");

                } else {
                    MyApplication.saveToPreferences(getContext(), SWITCH_8_AM, isChecked);

                    Intent intent = new Intent(getContext(), wiAlarmReceiver.class);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), MORNING_FILTER, intent, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
                    alarmManager.cancel(pendingIntent);
                    //notification dinlemeyi kapat..
                    Toast.makeText(getContext(),"Sabah 8 için alarm iptal edildi!",Toast.LENGTH_SHORT).show();

                }
            }
        });
        switch6PM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //alarmı kur
                    MyApplication.saveToPreferences(getContext(), SWITCH_6_PM, isChecked);

                    setNotification(18,NIGHT_FILTER,"Wi wii wii! Good Morning Service","Sabah oldu. Haydi nasıl giyineceğimize karar verme vakti!");

                } else {
                    MyApplication.saveToPreferences(getContext(), SWITCH_6_PM, isChecked);

                    Intent intent = new Intent(getContext(), wiAlarmReceiver.class);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), NIGHT_FILTER, intent, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
                    alarmManager.cancel(pendingIntent);
                    //notification dinlemeyi kapat..

                    Toast.makeText(getContext(),"Akşam 6 için alarm iptal edildi!",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


    private void setNotification(int hours,int requestCode,String title,String description) {
        Calendar calendar = Calendar.getInstance();
        // we can set time by open date and time picker dialog
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


        Intent myIntent = new Intent(getActivity(), wiAlarmReceiver.class);
        myIntent.putExtra("requestCode",requestCode);
        myIntent.putExtra("title",title);
        myIntent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                requestCode,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        if(hours == 8)
        {
            Toast.makeText(getContext(),"Sabah 8 için alarm kuruldu",Toast.LENGTH_SHORT).show();
        }
        else if(hours == 18){
            Toast.makeText(getContext(),"Akşam 6 için alarm kuruldu",Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG,hours+". saat için alarm kuruldu :)");
    }

    private void createNotification(int hours) {

        //pendingIntent = PendingIntent.getBroadcast()


        Calendar alarmStartTime = Calendar.getInstance();
        Log.d(TAG, "alarmStartTime" + alarmStartTime);

        Calendar now  = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY,hours);
        alarmStartTime.set(Calendar.MINUTE,0);
        alarmStartTime.set(Calendar.SECOND, 0);
        if(now.after(alarmStartTime))
        {
            Log.d(TAG,"Added a day");
        }

        Intent myIntent = new Intent(getContext(), wiAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, myIntent,0);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, alarmStartTime.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(logoutIntent);
                MyApplication.saveToPreferences(getContext(), "logged_in", false);
                MyApplication.saveToPreferences(getContext(), "UserID", -1);

                getActivity().finish();

            }
        });
    }


}
