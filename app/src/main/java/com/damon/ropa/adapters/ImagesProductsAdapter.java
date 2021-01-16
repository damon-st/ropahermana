package com.damon.ropa.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
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
    }

    @Override
    public int getItemCount() {
        return imagesLists.size();
    }
}
