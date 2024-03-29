package com.openclassroom.go4lunch.ui.restaurant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewModel> implements Serializable {
    public static final String RESTAURANT_INFO = "getRestaurantInfoWithExtra";
    Context context;
    List<Restaurant> mRestaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        mRestaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item
                , parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewModel holder, int position) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double userLatitude = location.getLatitude();
            double userLongitude = location.getLongitude();

            PlacesClient placesClient = Places.createClient(context);
            String placeId = mRestaurantList.get(position).getIdR();
            List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
            Task<FetchPlaceResponse> placeTask = placesClient.fetchPlace(request);
            placeTask.addOnSuccessListener(fetchPlaceResponse -> {
                Place place = fetchPlaceResponse.getPlace();
                LatLng placeLatLng = place.getLatLng();

                float[] distance = new float[1];
                assert placeLatLng != null;
                Location.distanceBetween(userLatitude, userLongitude, placeLatLng.latitude, placeLatLng.longitude,
                        distance);
                float distanceInMeters = distance[0];
                int finalDistance = (int) distanceInMeters;

                mRestaurantList.get(position).setDistanceToUser(finalDistance);
                holder.distance.setText(String.format("%sm", finalDistance));
            });
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String restaurantId = mRestaurantList.get(position).getIdR();
        CollectionReference usersRef = db.collection("workmates");
        Query query = usersRef.whereEqualTo("idSelectedRestaurant", restaurantId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int interestedWorkmates = task.getResult().size();
                mRestaurantList.get(position).setNumOfWorkmates(interestedWorkmates);
                holder.numOfColleagues.setText(String.format(Locale.ENGLISH, "(%d)", interestedWorkmates));
            } else {
                holder.numOfColleagues.setText("(0)");
                Log.d("RestaurantAdapter", "error getting documents: " + task.getException());
            }
        });

        if (mRestaurantList.get(position).isOpenNow()) {
            holder.openingHours.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.openingHours.setText(R.string.ouvert);
        } else {
            holder.openingHours.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.openingHours.setText(R.string.ferme);
        }

        holder.name.setText(mRestaurantList.get(position).getName());
        holder.address.setText(mRestaurantList.get(position).getAddress());
        Log.d("RestaurantAdapter", "onBindViewHolder: " + mRestaurantList.get(position).getRating());
        if (Double.isNaN(mRestaurantList.get(position).getRating())) {
            holder.ratingBar.setRating(0);
        } else {
            holder.ratingBar.setRating(mRestaurantList.get(position).getRating());
        }


        Glide.with(context)
                .load(mRestaurantList.get(position).getUrlPicture())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailedRestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO, mRestaurantList.get(position));
            v.getContext().startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortListByRating() {
        Comparator<Restaurant> ratingComparator = (o1, o2) -> {
            Double rating1 = Double.valueOf(o1.getRating());
            Double rating2 = Double.valueOf(o2.getRating());

            // Check if either rating is NaN
            if (rating1.isNaN() && rating2.isNaN()) {
                return 0; // Both ratings are NaN, so they are considered equal
            } else if (rating1.isNaN()) {
                return 1; // o1 has NaN rating, so it should be considered greater
            } else if (rating2.isNaN()) {
                return -1; // o2 has NaN rating, so it should be considered greater
            }

            // Compare ratings in descending order
            return Double.compare(rating2, rating1);
        };

        mRestaurantList.sort(ratingComparator);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortListByAlphabeticalOrder() {
        mRestaurantList.sort(Comparator.comparing(Restaurant::getName));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortListByDistanceToUser() {
        mRestaurantList.sort(Comparator.comparingDouble(Restaurant::getDistanceToUser));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortListByNumOfWorkmates() {
        mRestaurantList.sort((o1, o2) -> Double.compare(o2.getNumOfWorkmates(), o1.getNumOfWorkmates()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class RestaurantViewModel extends RecyclerView.ViewHolder {

        TextView name, address, openingHours, distance, numOfColleagues;
        ImageView image;
        RatingBar ratingBar;
        LinearLayout scrollableLinearLayout;

        public RestaurantViewModel(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.restaurant_name);
            address = itemView.findViewById(R.id.restaurant_address);
            openingHours = itemView.findViewById(R.id.restaurant_opening_hours);
            distance = itemView.findViewById(R.id.restaurant_distance);
            numOfColleagues = itemView.findViewById(R.id.restaurant_numOfColleagues);
            ratingBar = itemView.findViewById(R.id.restaurant_rating);
            image = itemView.findViewById(R.id.imageView);
            scrollableLinearLayout = itemView.findViewById(R.id.scrollable_linear_layout);
        }
    }
}
