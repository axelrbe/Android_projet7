package com.openclassroom.go4lunch.ui.restaurant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewModel> {
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
        return new RestaurantViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewModel holder, int position) {
        holder.name.setText(mRestaurantList.get(position).getName());
        holder.address.setText(mRestaurantList.get(position).getAddress());
        holder.distance.setText(mRestaurantList.get(position).getDistance());
        holder.type.setText(mRestaurantList.get(position).getType());
        holder.openingHours.setText(mRestaurantList.get(position).getOpeningHours());
        holder.numOfColleagues.setText(String.format("(%s)", mRestaurantList.get(position).getInterestedColleagues()));
        holder.ratingBar.setRating(mRestaurantList.get(position).getRating());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailedRestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO, mRestaurantList.get(position));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class RestaurantViewModel extends RecyclerView.ViewHolder {

        TextView name, address, type, openingHours, distance, numOfColleagues;
        RatingBar ratingBar;

        public RestaurantViewModel(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.restaurant_name);
            address = itemView.findViewById(R.id.restaurant_address);
            type = itemView.findViewById(R.id.restaurant_type);
            openingHours = itemView.findViewById(R.id.restaurant_opening_hours);
            distance = itemView.findViewById(R.id.restaurant_distance);
            numOfColleagues = itemView.findViewById(R.id.restaurant_numOfColleagues);
            ratingBar = itemView.findViewById(R.id.restaurant_rating);
        }
    }
}
