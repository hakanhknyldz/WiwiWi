package wiwiwi.io.wearwithweather;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import wiwiwi.io.wearwithweather.fragments.FragmentDrawer;
import wiwiwi.io.wearwithweather.fragments.FragmentProfile;
import wiwiwi.io.wearwithweather.fragments.FragmentWear;
import wiwiwi.io.wearwithweather.fragments.FragmentWeather;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.network.wiAlarmReceiver;
import wiwiwi.io.wearwithweather.network.wiService;
import wiwiwi.io.wearwithweather.pojo.UserDetails;


public class MainActivity extends AppCompatActivity implements MaterialTabListener, View.OnClickListener {
    private static final String TAG = "HAKKE";
    VolleyApplication volleyApplication;
    public static final int TAB_HOME = 0;
    public static final int TAB_USER = 1;
    public static final int TAB_MENU = 2;
    public static final int TAB_COUNT = 3;

    private static final int JOB_ID = 100;
    private static final String TAG_SORT_NAME = "sortName";
    private static final String TAG_SORT_DATE = "sortDate";
    private static final String TAG_SORT_RATINGS = "sortRatings";
    final int MORNING_FILTER = 1111;
    final int NIGHT_FILTER = 9999;

    private static final long POLL_FREQUENCY = 28800000;
    private Toolbar mToolbar;
    private ViewGroup mContainerToolbar;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private FragmentDrawer mDrawerFragment;
    Context context;
    String currentLocation = "";
    private String userGender = "";
    private String wiName,wiGender,wiSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTabs();
        setupDrawer();

        //getting userdetails from SharedPreferences..
        String userDetailsString = MyApplication.readFromPreferences(this,"userDetails",null);
        Gson gson = new Gson();
        UserDetails userDetails = gson.fromJson(userDetailsString,UserDetails.class);

        Log.d(TAG,"userDetails from SP :" + userDetails.getUsername());

        if(userDetails != null)
        {

            wiName = userDetails.getName();
            wiSurname = userDetails.getSurname();
            wiGender = userDetails.getGenderType();
        }

        volleyApplication = VolleyApplication.getInstance();
        volleyApplication.init(getApplicationContext());

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        currentLocation = latitude+","+longitude;
        Log.d(TAG, "MainACtivity -> currentLOcation : " + currentLocation);

        //wiService start
        Intent serviceStarter = new Intent(getBaseContext(),wiService.class);
        startService(serviceStarter);
        //wiBroadcastReceiver configuration
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver
                        (
                                wiBroadcastReceiver,
                                new IntentFilter("wi.action.getCurrentLocation")
                        );




        //ALARM MANAGER:....
/*
        setNotification(8,MORNING_FILTER,"Wi wii wii! Good Morning Service","Sabah oldu. Haydi nasıl giyineceğimize karar verme vakti!");
        setNotification(18,NIGHT_FILTER , "Wi wii Wiii! Night Service","Akşama hava nasıl mı, haydi görelim :)");
        setNotification(1,433 , "Wi wii Wiii! Night Service","Akşama hava nasıl mı, haydi görelim :)");
*/

    }

    private void setNotification(int hours,int requestCode,String title,String description) {
        Calendar calendar = Calendar.getInstance();
        // we can set time by open date and time picker dialog
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


        Intent myIntent = new Intent(MainActivity.this, wiAlarmReceiver.class);
        myIntent.putExtra("requestCode",requestCode);
        myIntent.putExtra("title",title);
        myIntent.putExtra("description",description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,requestCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(wiBroadcastReceiver);
    }

    private BroadcastReceiver wiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intentFromWiService) {
            currentLocation = intentFromWiService.getStringExtra("currentLocation");
        }
    };



    private void setupDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
    }

    public void onDrawerItemClicked(int index) {
        mPager.setCurrentItem(index);
    }

    public View getContainerToolbar() {
        return mContainerToolbar;
    }

    private void setupTabs() {
        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);

            }
        });
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setIcon(mAdapter.getIcon(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Hey you just hit + " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.navigate) {
            //startActivity(new Intent(this, ShoppingCartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        mPager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    @Override
    public void onClick(View v) {
        //call instantiate item since getItem may return null depending on whether the PagerAdapter is of type FragmentPagerAdapter or FragmentStatePagerAdapter
        Fragment fragment = (Fragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());

    }

    /* CREATING PAGER ADAPTER FOR TABS */
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int icons[] = {R.drawable.ic_action_sun,
                R.drawable.ic_action_user_white,
                R.drawable.ic_action_tshirt
                };

        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            Fragment fragment = null;
            switch (num) {
                case TAB_HOME:
                    fragment = FragmentWeather.newInstance(currentLocation, wiGender);
                    break;
                case TAB_USER:
                    fragment = FragmentProfile.newInstance("", "");
                    break;
                case TAB_MENU:
                    fragment = FragmentWear.newInstance(wiGender,"");
                    break;

            }
            return fragment;

        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabs)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
    }

}
