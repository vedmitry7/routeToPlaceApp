package com.vedmitryapps.makeittestapp.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vedmitryapps.makeittestapp.Constants;
import com.vedmitryapps.makeittestapp.R;
import com.vedmitryapps.makeittestapp.SharedManager;
import com.vedmitryapps.makeittestapp.api.ApiFactory;
import com.vedmitryapps.makeittestapp.api.models.PlacesResponce;
import com.vedmitryapps.makeittestapp.api.models.Result;
import com.vedmitryapps.makeittestapp.views.adapters.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_container)
    RelativeLayout progressLayout;

    RecyclerAdapter adapter;
    private List<Result> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedManager.init(this);


        results = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(results);
        recyclerView.setAdapter(adapter);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getLastKnownLocation();
                        }
                        if(report.getDeniedPermissionResponses().size()>0){
                            hideProgress();
                            Toast.makeText(MainActivity.this, getString(R.string.cant_work_without_perm), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    void getLastKnownLocation(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            SharedManager.addProperty("lat", String.valueOf(location.getLatitude()));
                            SharedManager.addProperty("lng", String.valueOf(location.getLongitude()));
                            loadPlaces(location.getLatitude() + "," + location.getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "Find location problem", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    }
                });
    }

    private void loadPlaces(String location) {

        ApiFactory.getApi().getPlaces(Constants.API_KEY, location, 1000).enqueue(new Callback<PlacesResponce>() {
            @Override
            public void onResponse(Call<PlacesResponce> call, Response<PlacesResponce> response) {
                if(response.body()!=null && response.body().getResults()!=null){
                    adapter.update(response.body().getResults());
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<PlacesResponce> call, Throwable t) {
                Log.d("TAG21", t.getLocalizedMessage());
                hideProgress();
                Toast.makeText(MainActivity.this, "Request api error", Toast.LENGTH_LONG).show();
            }
        });
    }

    void hideProgress(){
        progressLayout.setVisibility(View.GONE);
    }
}
