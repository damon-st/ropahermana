package com.damon.ropa.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;

public class ImagesUrl  extends RecyclerView.ViewHolder {

   public ImageView img_url,delete_img;

    public ImagesUrl(@NonNull View itemView) {
        super(itemView);
        img_url = itemView.findViewById(R.id.img_url);
        delete_img = itemView.findViewById(R.id.delete_img);
    }
}
