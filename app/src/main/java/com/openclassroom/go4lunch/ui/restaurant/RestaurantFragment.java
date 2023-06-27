package com.openclassroom.go4lunch.ui.restaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;

import java.io.Serializable;

public class RestaurantFragment extends Fragment implements Serializable {

    RecyclerView mRestaurantRecyclerView;
    RestaurantAdapter mRestaurantAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRestaurantRecyclerView = root.findViewById(R.id.list_recycler_view);

        RestaurantRepository.getInstance().getAllRestaurant().observe(requireActivity(), restaurants -> {
            mRestaurantAdapter = new RestaurantAdapter(getActivity(), restaurants);
            mRestaurantRecyclerView.setAdapter(mRestaurantAdapter);
            mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    RecyclerView.VERTICAL, false));
            mRestaurantRecyclerView.setHasFixedSize(true);
            mRestaurantRecyclerView.setNestedScrollingEnabled(false);
        });
        return root;
    }
}