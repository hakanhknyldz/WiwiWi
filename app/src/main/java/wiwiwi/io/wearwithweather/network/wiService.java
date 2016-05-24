package wiwiwi.io.wearwithweather.network;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by dilkom-hak on 11.05.2016.
 */
public class wiService extends Service {

    private static final String TAG = "TAG";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(provider, 60000, 20, new LocationListener() {
            @Override
            public void onLocationChanged(Location current) {
                double latitude, longitude;
                latitude = current.getLatitude();
                longitude = current.getLongitude();

                Log.d(TAG, String.format("wiService => onLocationChanged => Current: {},{}", latitude, longitude));

                Intent serviceIntent = new Intent();
                serviceIntent.setAction("wi.action.getCurrentLocation");

                serviceIntent.putExtra("currentLocation", latitude + "," + longitude);

                //sendBroadcast(intent);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(serviceIntent);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
