package com.openclassroom.go4lunch.ui.workmates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.ui.HomeActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WorkmatesFragment extends Fragment {

    private RecyclerView mWorkmatesRecyclerView;
    private WorkmatesAdapter mWorkmatesAdapter;
    private List<Workmates> mWorkmatesList;
    private Restaurant selectedRestaurant;
    private FirebaseUser currentUser;
    private String userId;
    private SearchView searchView;
    private CardView autocompleteContainer;
    private ImageButton searchViewBtn;
    Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        context = container.getContext();

        mWorkmatesRecyclerView = root.findViewById(R.id.workmates_recycler_view);

        if (getActivity() instanceof HomeActivity) {
            searchView = ((HomeActivity) getActivity()).getSearchView();
            autocompleteContainer = ((HomeActivity) getActivity()).getAutocompleteContainer();
            searchViewBtn = ((HomeActivity) getActivity()).getSearchViewBtn();
        }

        autocompleteContainer.setVisibility(View.GONE);

        searchViewBtn.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.VISIBLE) {
                searchView.setVisibility(View.GONE);
            } else {
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                searchView.setIconified(false);
                searchView.setOnCloseListener(() -> {
                    searchView.setVisibility(View.GONE);
                    return true;
                });
            }
        });

        setUpWorkmatesSearch();
        displayListOfWorkmates();

        return root;
    }

    private void displayListOfWorkmates() {
        RestaurantRepository.getInstance().getAllRestaurant().observe(requireActivity(), restaurants -> {
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

                                    Comparator<Workmates> workmatesComparator = (o1, o2) -> {
                                        if (o1.getRestaurant() != null && o2.getRestaurant() == null) {
                                            return -1;
                                        } else if (o1.getRestaurant() == null && o2.getRestaurant() != null) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    };

                                    mWorkmatesList.sort(workmatesComparator);

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
    }

    private void setUpWorkmatesSearch() {
        AutoCompleteTextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setTextColor(Color.WHITE);
        searchText.setHintTextColor(Color.WHITE);
        searchText.setTextSize(20);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchWorkmates(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            displayListOfWorkmates();
            return false;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchWorkmates(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query searchQuery = db.collection("workmates")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff");

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mWorkmatesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Workmates workmate = document.toObject(Workmates.class);
                    mWorkmatesList.add(workmate);
                }
                mWorkmatesAdapter.notifyDataSetChanged();
            } else {
                Log.d("WorkmatesFragment", "searchWorkmates: failed to search workmates");
            }
        });
    }

}