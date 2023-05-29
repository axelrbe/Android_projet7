package com.openclassroom.go4lunch.ui.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewModel> {
    Context context;
    List<Workmates> mWorkmatesList;

    public WorkmatesAdapter(Context context, List<Workmates> workmatesList) {
        this.context = context;
        mWorkmatesList = workmatesList;
    }

    @NonNull
    @Override
    public WorkmatesViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_item, parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewModel holder, int position) {
        holder.workmatesInfos.setText(String.format("%s is eating %s (%s)", mWorkmatesList.get(position).getName(), mWorkmatesList.get(position).getRestaurant().getType(), mWorkmatesList.get(position).getRestaurant().getName()));
    }

    @Override
    public int getItemCount() {
        return mWorkmatesList.size();
    }

    public static class WorkmatesViewModel extends RecyclerView.ViewHolder {

        TextView workmatesInfos;

        public WorkmatesViewModel(@NonNull View itemView) {
            super(itemView);

            workmatesInfos = itemView.findViewById(R.id.workmates_infos);
        }
    }
}
