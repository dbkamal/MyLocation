package com.example.android.mylocation;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.jar.Manifest;
import android.Manifest.permission;

import org.w3c.dom.Text;

/** This Activity connects with Google Play Services and display the current Latitude and Langitude
 *  Implement ConnectionCallbacks an OnConnectionFailedListener
 * */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final String LOG_TAG = "MyLocation";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private TextView mTextViewLatitude;
    private TextView mTextViewLangitude;
    public static final int MY_PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Create Google Api Client using Builder Constructor and Add all the listeners and Location Service API */
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API) //Add the Location Service API
                    .build();
        }

        mTextViewLatitude = (TextView) findViewById(R.id.latitude_text);
        mTextViewLangitude = (TextView) findViewById(R.id.langitude_text);
    }

    /** This method will connect to Google Play Service */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /** This method will disconnect from Google Play Service if it's still connected */
    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    /** If Google Play Service connection is Successful this Callback method will be fired Asynchronously
     *
     *  Param: Bundle of data provided to clients by Google Play services. May be null if no content is provided by the service
     * */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try{

            /** Request the permissions you need. First check if the self permission is granted or denied
             *
             * ContextCompat.checkSelfPermission - Determine whether you have been granted a particular permission
             *
             * Param: Context and String (Name of the permission)
             * */
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                /** As User's location comes under Dangerous permission, System will explicitly prompt User's permission on Run time
                 *
                 * ActivityCompat.requestPermissions - Request permission to be granted to this application
                 *
                 * Param: Activity, String[] permission, int RequestCode
                 * */
                ActivityCompat.requestPermissions(this, new String[] {permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
            else {

                /** Custom method to pull the Current Location and set the Latitude and Langitude */
                getMyLocation();
            }
        }
        catch(Exception e){
            Log.v(LOG_TAG, e.toString());
        }
    }


    /** This Callback method will be fired for the result from Requesting Permission
     *
     *  Param: int RequestCode (which is passed in requestPermissions method), String[] permission,
     *                                  int[] grantResult (PERMISSION_GRANTED/PERMISSION_DENIED)
     *
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                //If User cancelled the request, the array will be empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getMyLocation();
                }
                else {
                    Log.v(LOG_TAG, "Permission denied");
                }
                return;
            }
        }
    }

    /** This method will be fired if Google Play Services connected suspended */
    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "Connection Suspended");
    }

    /** This method will be fired if Google Play Services connected failed */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(LOG_TAG, connectionResult.getErrorMessage());
    }

    public void getMyLocation() {
        /** User Granted the permission already to check the latest location of the User*/
        try {

            /** LocationService - The main entry point for location service Integration
             *  FusedLocationProviderApi - Entry point for interacting Fused location provider
             *  getLastLocation - Returns the best most recent location currently available
             *
             *  Param: GoogleApiClient Object
             *
             *  Return: Location object
             *
             *  */
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLocation != null) {
                mTextViewLatitude.setText(String.valueOf(mLocation.getLatitude()));
                mTextViewLangitude.setText(String.valueOf(mLocation.getLongitude()));
            }
        }
        catch(SecurityException e){
            Log.v(LOG_TAG, e.toString());
        }
    }
}
