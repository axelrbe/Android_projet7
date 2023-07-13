package com.openclassroom.go4lunch.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.ui.restaurant.DetailedRestaurantActivity;
import com.openclassroom.go4lunch.ui.restaurant.RestaurantAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements Serializable {
    public SupportMapFragment mapFragment;
    Context context;
    public GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    List<Restaurant> mRestaurantList = new ArrayList<>();
    private MarkerOptions mMarkerOptions;
    public MapFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        context = container.getContext();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        RestaurantRepository.getInstance().getAllRestaurant().observe(requireActivity(), restaurants -> {
            for (Restaurant restaurant : restaurants) {
                mRestaurantList.add(restaurant);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                CollectionReference restaurantsRef = firebaseFirestore.collection("workmates");

                restaurantsRef.whereEqualTo("idSelectedRestaurant", restaurant.getIdR())
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (QueryDocumentSnapshot ignored : querySnapshot) {
                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(restaurant.getLatLng())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title(restaurant.getName())
                                        .snippet(restaurant.getAddress())
                                        .anchor(0.5f, 0.5f)
                                        .flat(true));
                            }
                        })
                        .addOnFailureListener(e -> Log.e("MapFragment", "Error getting selected restaurants", e));

                mMarkerOptions = new MarkerOptions()
                        .position(restaurant.getLatLng())
                        .title(restaurant.getName())
                        .snippet(restaurant.getAddress())
                        .anchor(0.5f, 0.5f)
                        .flat(true);

                mGoogleMap.addMarker(mMarkerOptions);
            }

            mGoogleMap.setOnMarkerClickListener(marker -> {
                Restaurant selectedRestaurant = getRestaurantByMarker(marker);
                assert selectedRestaurant != null;
                marker.setTitle(selectedRestaurant.getName());
                marker.setSnippet(selectedRestaurant.getAddress());
                marker.showInfoWindow();
                return true;
            });

            mGoogleMap.setOnInfoWindowClickListener(marker -> {
                Restaurant selectedRestaurant = getRestaurantByMarker(marker);

                Intent intent = new Intent(requireContext(), DetailedRestaurantActivity.class);
                intent.putExtra(RestaurantAdapter.RESTAURANT_INFO, selectedRestaurant);
                startActivity(intent);
            });
        });

        moveTheCameraToCurrentLocation();
        return root;
    }

    private Restaurant getRestaurantByMarker(Marker marker) {
        for (Restaurant restaurant : mRestaurantList) {
            LatLng latLng = new LatLng(restaurant.getLatLng().latitude, restaurant.getLatLng().longitude);
            if (marker.getPosition().equals(latLng)) {
                return restaurant;
            }
        }

        return null;
    }

    private void moveTheCameraToCurrentLocation() {
        mLocationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = location -> {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, mLocationListener);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setMyLocationEnabled(true);
            mGoogleMap = googleMap;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, mLocationListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop location updates
        mLocationManager.removeUpdates(mLocationListener);
    }
}