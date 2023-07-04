package com.openclassroom.go4lunch.ui.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;

import java.io.Serializable;

public class RestaurantFragment extends Fragment implements Serializable {

    RecyclerView mRestaurantRecyclerView;
    RestaurantAdapter mRestaurantAdapter;
    private FloatingActionButton filterListBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRestaurantRecyclerView = root.findViewById(R.id.list_recycler_view);

        filterListBtn = root.findViewById(R.id.filter_list_btn);

        filterListBtn.setOnClickListener(v -> allFiltersManagement());
        updateListOfRestaurant();
        return root;
    }

    private void allFiltersManagement() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), filterListBtn);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if ((item.getItemId()) == R.id.menu_item_rating) {
                mRestaurantAdapter.sortListByRating();
                return true;
            } else if ((item.getItemId()) == R.id.menu_item_distance) {
                mRestaurantAdapter.sortListByDistanceToUser();
                return true;
            } else if ((item.getItemId()) == R.id.menu_item_alphabetical) {
                mRestaurantAdapter.sortListByAlphabeticalOrder();
                return true;
            } else if ((item.getItemId()) == R.id.menu_item_workmates) {
                mRestaurantAdapter.sortListByNumOfWorkmates();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void updateListOfRestaurant() {
        RestaurantRepository.getInstance().getAllRestaurant().observe(requireActivity(), restaurants -> {
            mRestaurantAdapter = new RestaurantAdapter(getActivity(), restaurants);
            mRestaurantRecyclerView.setAdapter(mRestaurantAdapter);
            mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    RecyclerView.VERTICAL, false));
            mRestaurantRecyclerView.setHasFixedSize(true);
            mRestaurantRecyclerView.setNestedScrollingEnabled(false);
        });
    }
}