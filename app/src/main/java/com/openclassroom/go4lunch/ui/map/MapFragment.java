package com.openclassroom.go4lunch.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;

import java.io.Serializable;

public class MapFragment extends Fragment implements Serializable {
    private SupportMapFragment mapFragment;
    Context context;
    private GoogleMap mGoogleMap;

    public MapFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        context = container.getContext();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        RestaurantRepository.getInstance().getAllRestaurant().observe(requireActivity(), restaurants -> {
            for (Restaurant restaurant : restaurants) {
                Log.d("mapFragment", "Latitude and Longitude of restaurants " + restaurant.getLocation());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude()))
                        .title(restaurant.getName())
                        .snippet(restaurant.getAddress());
                mGoogleMap.addMarker(markerOptions);
            }
        });

        Dexter.withContext(context).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                getCurrentLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                           PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        return root;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setMyLocationEnabled(true);
            mGoogleMap = googleMap;
        });
    }
}