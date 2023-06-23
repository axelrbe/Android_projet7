package com.openclassroom.go4lunch.ui.restaurant;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewModel> implements Serializable{
    public static final String RESTAURANT_INFO = "getRestaurantInfoWithExtra";
    Context context;
    List<Restaurant> mRestaurantList;
    FirebaseUser currentUser;
    String userId;
    private int interestedColleagues = 0;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        mRestaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewModel holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert currentUser != null;
                    userId = currentUser.getUid();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!userId.equals(document.getId())) {
                                if (Objects.equals(document.getString("idSelectedRestaurant"),
                                        mRestaurantList.get(position).getIdR())) {
                                    interestedColleagues++;
                                    holder.numOfColleagues.setText(String.format(Locale.ENGLISH, "(%d)", interestedColleagues));
                                } else {
                                    holder.numOfColleagues.setText("(0)");
                                }
                            }
                        }
                    } else {
                        Log.d("RestaurantAdapter", "Error getting documents: ", task.getException());
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
        holder.distance.setText(String.format("%sm", "100"));
        holder.type.setText(mRestaurantList.get(position).getType());
        holder.ratingBar.setRating(mRestaurantList.get(position).getRating());

        Glide.with(context)
                .load(mRestaurantList.get(position).getUrlPicture())
                .into(holder.image);

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
        ImageView image;
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
            image = itemView.findViewById(R.id.imageView);
        }
    }
}
