package com.openclassroom.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.models.entities.PlacesResults;
import com.openclassroom.go4lunch.models.entities.Result;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.retrofit.ApiClient;
import com.openclassroom.go4lunch.retrofit.GoogleMapAPI;
import com.openclassroom.go4lunch.ui.restaurant.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkmatesFragment extends Fragment {

    RecyclerView mWorkmatesRecyclerView;
    WorkmatesAdapter mWorkmatesAdapter;
    List<Workmates> mWorkmatesList;
    Restaurant selectedRestaurant;
    FirebaseUser currentUser;
    String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        mWorkmatesRecyclerView = root.findViewById(R.id.workmates_recycler_view);

        RestaurantRepository.getInstance(requireContext()).getAllRestaurant().observe(requireActivity(), restaurants -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("workmates")
                    .get()
                    .addOnCompleteListener(task -> {
                        currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert currentUser != null;
                        userId = currentUser.getUid();
                        if (task.isSuccessful()) {
                            mWorkmatesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!userId.equals(document.getId())) {
                                    for (int i = 0; i < restaurants.size(); i++) {
                                        if (Objects.equals(document.getString("idSelectedRestaurant"),
                                                restaurants.get(i).getIdR())) {
                                            selectedRestaurant = restaurants.get(i);
                                        }
                                    }
                                    Workmates workmate = new Workmates(document.getId(),
                                            document.getString("name"),
                                            document.getString("profilePicture"),
                                            document.getString("email"),
                                            selectedRestaurant,
                                            false);
                                    mWorkmatesList.add(workmate);

                                    mWorkmatesAdapter = new WorkmatesAdapter(getActivity(), mWorkmatesList);
                                    mWorkmatesRecyclerView.setAdapter(mWorkmatesAdapter);
                                    mWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                                            RecyclerView.VERTICAL, false));
                                    mWorkmatesRecyclerView.setHasFixedSize(true);
                                    mWorkmatesRecyclerView.setNestedScrollingEnabled(false);
                                }
                            }
                        } else {
                            Log.d("workmatesFragment", "Error getting documents: ", task.getException());
                        }
                    });
        });
        return root;
    }
}