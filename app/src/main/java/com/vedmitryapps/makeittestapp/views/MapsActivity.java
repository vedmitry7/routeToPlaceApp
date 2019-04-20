package com.vedmitryapps.makeittestapp.views;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.vedmitryapps.makeittestapp.Constants;
import com.vedmitryapps.makeittestapp.R;
import com.vedmitryapps.makeittestapp.SharedManager;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int width;
    private LatLng myLocation;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initPoints();

        width = getResources().getDisplayMetrics().widthPixels;
    }

    private void initPoints() {
        Double lat, lng;
        if(getIntent()!=null){
            lat = getIntent().getDoubleExtra("lat",0);
            lng = getIntent().getDoubleExtra("lng",0);
            destination = new LatLng(lat, lng);
        }
        lat = Double.parseDouble(SharedManager.getProperty("lat"));
        lng = Double.parseDouble(SharedManager.getProperty("lng"));
        myLocation = new LatLng(lat, lng);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        createRoute();
    }

    void createRoute(){

        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(Constants.API_KEY)
                .build();

        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(myLocation)
                    .destination(destination)
                    .await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        }

        List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();
        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < path.size(); i++) {
            line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
            latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
        }

        line.width(10f).color(R.color.colorPrimary);

        mMap.addPolyline(line);

        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width - width/5, width, 25);
        mMap.moveCamera(track);

        mMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(myLocation.lat, myLocation.lng)));
        mMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(destination.lat,destination.lng)));
    }

}
