package net.kon.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.kon.map.consts.Consts;

import java.util.List;

/**
 * Created by kon on 2017/4/6.
 *
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private final static String TAG = "MapActivity";

    private TextView mTxtAddress;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_map);

        this.init();
        this.initComponent();
        this.initEvent();
    }

    /**
     *
     *
     */
    private void init() {
        mLocationRequest = new LocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * init layout component
     */
    private void initComponent() {
        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mMapFragment = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map));
    }

    /**
     * init event
     *
     */
    protected void initEvent() {
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapReady();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Consts.MAP_READY_PERMISSION);
            return;
        }
    }

    /**
     * 當地圖準備好時要做哪些事
     *
     */
    public void mapReady() {
        try {
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    double lat = mMap.getCameraPosition().target.latitude;
                    double lng = mMap.getCameraPosition().target.longitude;
                    getAddress(lat, lng);
                }
            });

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setRotateGesturesEnabled(false);
            uiSettings.setTiltGesturesEnabled(false);
            mGoogleApiClient.connect();
        }
        catch(SecurityException e) {
            Log.e(TAG, "error", e);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(Consts.REQUEST_LOCATION_TIME);
//        mLocationRequest.setFastestInterval(Consts.REQUEST_LOCATION_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), Consts.MAP_DEFAULT_ZOOM));
    }

    /**
     * get address
     *
     * @param lat
     * @param lng
     */
    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocation(lat, lng, 1);
            if(address!=null && address.size()>=1) {
                Address addr = address.get(0);
                mTxtAddress.setText(addr.getAddressLine(0));
            }
        }
        catch(Exception e ) {
            Log.e(TAG, "error", e);
        }
    }
}
