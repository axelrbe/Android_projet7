package com.openclassroom.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.injection.DI;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.services.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesFragment extends Fragment {

    RecyclerView mWorkmatesRecyclerView;
    WorkmatesAdapter mWorkmatesAdapter;
    List<Workmates> mWorkmatesList;
    ApiService mApiService;
    List<Restaurant> allRestaurants;
    Restaurant selectedRestaurant;
    FirebaseUser currentUser;
    String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        mApiService = DI.getApiService();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    userId = currentUser.getUid();
                    allRestaurants = mApiService.getAllRestaurants();
                    if (task.isSuccessful()) {
                        mWorkmatesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!userId.equals(document.getId())) {
                                for (int i = 0; i < allRestaurants.size(); i++) {
                                    if (Objects.equals(document.getLong("idSelectedRestaurant"), allRestaurants.get(i).getId())) {
                                        selectedRestaurant = allRestaurants.get(i);
                                    }
                                }
                                Workmates workmate = new Workmates(document.getId(),
                                        document.getString("name"),
                                        document.getString("profilePicture"),
                                        document.getString("email"),
                                        selectedRestaurant,
                                        false);
                                mWorkmatesList.add(workmate);

                                mWorkmatesRecyclerView = root.findViewById(R.id.workmates_recycler_view);
                                mWorkmatesAdapter = new WorkmatesAdapter(getActivity(), mWorkmatesList);
                                mWorkmatesRecyclerView.setAdapter(mWorkmatesAdapter);
                                mWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                                mWorkmatesRecyclerView.setHasFixedSize(true);
                                mWorkmatesRecyclerView.setNestedScrollingEnabled(false);
                            }
                        }
                    } else {
                        Log.d("usersInfos", "Error getting documents: ", task.getException());
                    }
                });

        return root;
    }
}