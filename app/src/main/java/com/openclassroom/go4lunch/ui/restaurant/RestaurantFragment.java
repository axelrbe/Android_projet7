package com.openclassroom.go4lunch.ui.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.injection.DI;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.services.ApiService;

import java.util.List;

public class RestaurantFragment extends Fragment {

    RecyclerView mRestaurantRecyclerView;
    RestaurantAdapter mRestaurantAdapter;
    List<Restaurant> mRestaurantList;
    ApiService mApiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        mApiService = DI.getApiService();

        mRestaurantRecyclerView = root.findViewById(R.id.list_recycler_view);
        mRestaurantList = mApiService.getAllRestaurants();
        mRestaurantAdapter = new RestaurantAdapter(getActivity(), mRestaurantList);
        mRestaurantRecyclerView.setAdapter(mRestaurantAdapter);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantRecyclerView.setNestedScrollingEnabled(false);

        return root;
    }
}