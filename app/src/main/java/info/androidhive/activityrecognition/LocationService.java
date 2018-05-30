package info.androidhive.activityrecognition;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;

public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final IBinder mBinder = new LocalBinder();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000;
    public static double distance;
    private Location mCurrentLocation, lStart, lEnd;
    private double speed;
    public final String TAG = "S E R V I C E :";

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(mLocationRequest);
//
//            builder.setAlwaysShow(true);
//
//            PendingResult<LocationSettingsResult> result =
//                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                @Override
//                public void onResult(LocationSettingsResult result) {
//                    final Status status = result.getStatus();
//                    switch (status.getStatusCode()) {
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            try {
//                                status.startResolutionForResult(GPS.this, 199);
//                            } catch (IntentSender.SendIntentException e) {
//
//                            }
//                            break;
//                    }
//                }
//            });
//        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        Toast.makeText(this, "createLocationRequest()", Toast.LENGTH_SHORT).show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        distance=0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand()", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "onLocationChanged()", Toast.LENGTH_SHORT).show();
        mCurrentLocation = location;
        if (lStart==null)
            lStart = lEnd = mCurrentLocation;
        else
            lEnd = mCurrentLocation;
        updateUI();
        speed = location.getSpeed()*(3.6);
        Toast.makeText(this, "Speed = "+String.valueOf(speed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected()");
        Toast.makeText(this, "onConnected()", Toast.LENGTH_SHORT).show();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException s) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed()", Toast.LENGTH_SHORT).show();
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    private void updateUI() {
        Log.e(TAG, "updateUI()");
        Toast.makeText(this, "updateUI()", Toast.LENGTH_SHORT).show();
        distance += lStart.distanceTo(lEnd)/1000.00;
        GPS.endTime = System.currentTimeMillis();
        long diff = GPS.endTime - GPS.startTime;
        GPS.time.setText("Time = "+diff);
        GPS.speed.setText("Speed = "+ speed);
        GPS.dist.setText("Distance = "+distance);
        GPS.lat.setText(String.valueOf(mCurrentLocation.getLatitude()));
        GPS.lon.setText(String.valueOf(mCurrentLocation.getLongitude()));
    }
}
