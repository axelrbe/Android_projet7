package com.openclassroom.go4lunch.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.services.ApiService;

import java.util.List;
import java.util.Objects;

public class WorkmatesRepository {
    private final ApiService mApiService;

    public WorkmatesRepository(ApiService apiService) {mApiService = apiService;}

    public List<Workmates> getWorkmatesList() {return mApiService.getAllWorkmates();}
}
