package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapSecCab extends FragmentActivity implements OnClickListener {
    private MobileSKDApp mMobileSKDApp;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    Button button;

    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setContentView(R.layout.maps_sec_cab);
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(false);

        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        button=(Button)findViewById(R.id.bk);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        Log.d("that how you do it","2");

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);*/

       locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,  10, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
           // showLocation(location);

     //      drawMarker(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }


        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
           /* if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }*/
        }
    };

    private void showLocation(Location location) {
        /*if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }*/
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f",
                location.getLatitude(), location.getLongitude());
    }

    private void checkEnabled() {
     /*   tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));*/
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        mMap.isBuildingsEnabled();
        mMap.isMyLocationEnabled();
    }

    /**
     Здесь можно ставить маркеры и центрировать карту
     */
    private void setUpMap() {
        /*CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(53.383333, 59.033333));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);*/
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(53.416139, 59.055231) ,12) );
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.415535, 59.053512)).title("Проходная 1"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.411036, 59.051030)).title("Проходная 2"));/*53.418677, 59.047866*/
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.421936, 59.066590)).title("Проходная 3"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.444358, 59.058343)).title("Проходная 4"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.413929, 59.036165)).title("Склад 50"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.418024, 59.046422)).title("ЭСПЦ Пост 1"));
    }

    /* Вычисляем координаты и приближаемся */
   private void drawMarker(Location location){
        mMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
       Log.d("***********************************************************************", formatLocation(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,16));
        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:"+ location.getLongitude()));
   }

}
