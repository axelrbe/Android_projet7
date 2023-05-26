package com.openclassroom.go4lunch.ui.workmates;

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
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.services.ApiService;

import java.util.List;

public class WorkmatesFragment extends Fragment {

    RecyclerView mWorkmatesRecyclerView;
    WorkmatesAdapter mWorkmatesAdapter;
    List<Workmates> mWorkmatesList;
    ApiService mApiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        mApiService = DI.getApiService();

        mWorkmatesRecyclerView = root.findViewById(R.id.workmates_recycler_view);
        mWorkmatesList = mApiService.getAllWorkmates();
        mWorkmatesAdapter = new WorkmatesAdapter(getActivity(), mWorkmatesList);
        mWorkmatesRecyclerView.setAdapter(mWorkmatesAdapter);
        mWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mWorkmatesRecyclerView.setHasFixedSize(true);
        mWorkmatesRecyclerView.setNestedScrollingEnabled(false);

        return root;
    }
}