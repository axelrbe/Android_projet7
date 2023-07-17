package com.openclassroom.go4lunch.ui.workmates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.openclassroom.go4lunch.R;
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
        return new WorkmatesViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewModel holder, int position) {
        String notDecided = "hasn't decided yet";
        if (mWorkmatesList.get(position).getRestaurant() != null) {
            holder.workmatesInfos.setText(String.format("%s is eating at %s",
                    mWorkmatesList.get(position).getName(),
                    mWorkmatesList.get(position).getRestaurant().getName()));
        } else {
            holder.workmatesInfos.setTextColor(Color.GRAY);
            holder.workmatesInfos.setText(String.format("%s %s",
                    mWorkmatesList.get(position).getName(),
                    notDecided));
        }

        String url = mWorkmatesList.get(position).getUrlPicture();
        Glide.with(this.context)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.workmatesImage.setImageBitmap(resource);
                        holder.workmatesImage.buildDrawingCache();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mWorkmatesList.size();
    }

    public static class WorkmatesViewModel extends RecyclerView.ViewHolder {

        TextView workmatesInfos;
        ImageView workmatesImage;

        public WorkmatesViewModel(@NonNull View itemView) {
            super(itemView);

            workmatesInfos = itemView.findViewById(R.id.workmates_infos);
            workmatesImage = itemView.findViewById(R.id.workmates_image);
        }
    }
}
