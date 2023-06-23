package com.openclassroom.go4lunch.repositories;


import com.google.firebase.firestore.QuerySnapshot;
import com.openclassroom.go4lunch.models.Restaurant;

import java.util.Calendar;
import java.util.Date;

public class WorkmatesRepository {

    private static volatile WorkmatesRepository instance;
    public static WorkmatesRepository getInstance() {
        if(instance==null)
        {
            instance=new WorkmatesRepository();
        }
        return instance;
    }

}
