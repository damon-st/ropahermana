package com.damon.ropa.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.activitys.ImageViewerActivity;
import com.damon.ropa.holder.ImagesUrl;
import com.damon.ropa.models.ImagesList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesProductsAdapter extends RecyclerView.Adapter<ImagesUrl> {
    List<ImagesList> imagesLists ;

    Activity activity;

    public ImagesProductsAdapter(List<ImagesList> imagesLists, Activity activity) {
        this.imagesLists = imagesLists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ImagesUrl onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.img_layout,parent,false);
        return new ImagesUrl(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesUrl holder, int position) {
        holder.delete_img.setVisibility(View.GONE);
        Picasso.get().load(imagesLists.get(position).getUrl()).into(holder.img_url);

        holder.img_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(activity, ImageViewerActivity.class);
                intent.putExtra("url",imagesLists.get(position).getUrl());
                ActivityOptionsCompat  compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.img_url, ViewCompat.getTransitionName(holder.img_url));
                activity.startActivity(intent,compat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesLists.size();
    }
}
