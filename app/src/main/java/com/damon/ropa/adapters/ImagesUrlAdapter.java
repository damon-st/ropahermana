package com.damon.ropa.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.damon.ropa.holder.ImagesUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagesUrlAdapter extends RecyclerView.Adapter<ImagesUrl> {

    List<String> list = new ArrayList<>();
    Activity activity;

    public ImagesUrlAdapter(List<String> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ImagesUrl onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.img_url_layout,parent,false);
        return new ImagesUrl(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesUrl holder, int position) {
        Picasso.get().load(list.get(position)).resize(200,200).into(holder.img_url);
        holder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
