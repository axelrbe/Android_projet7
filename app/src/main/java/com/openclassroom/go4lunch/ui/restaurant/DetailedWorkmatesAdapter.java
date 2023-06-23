package com.openclassroom.go4lunch.ui.restaurant;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.Serializable;
import java.util.List;

public class DetailedWorkmatesAdapter extends RecyclerView.Adapter<DetailedWorkmatesAdapter.DetailedWorkmatesViewModel> implements Serializable {
    Context context;
    List<Workmates> mDetailedWorkmatesList;

    public DetailedWorkmatesAdapter(Context context, List<Workmates> workmatesList) {
        this.context = context;
        mDetailedWorkmatesList = workmatesList;
    }

    @NonNull
    @Override
    public DetailedWorkmatesViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailedWorkmatesAdapter.DetailedWorkmatesViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedWorkmatesViewModel holder, int position) {
        holder.workmatesInfos.setText(String.format("%s is joining !", mDetailedWorkmatesList.get(position).getName()));

        String url = mDetailedWorkmatesList.get(position).getUrlPicture();
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
        return mDetailedWorkmatesList.size();
    }

    public static class DetailedWorkmatesViewModel extends RecyclerView.ViewHolder {

        TextView workmatesInfos;
        ImageView workmatesImage;

        public DetailedWorkmatesViewModel(@NonNull View itemView) {
            super(itemView);

            workmatesInfos = itemView.findViewById(R.id.workmates_infos);
            workmatesImage = itemView.findViewById(R.id.workmates_image);
        }
    }
}
