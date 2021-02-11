package com.damon.ropa.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;

public class ImagesUrl  extends RecyclerView.ViewHolder {

   public ImageView img_url,delete_img,delete_video,voice_off;
   public FrameLayout video_layout, image_layout;
   public VideoView videoPath;

    public ImagesUrl(@NonNull View itemView) {
        super(itemView);
        img_url = itemView.findViewById(R.id.img_url);
        delete_img = itemView.findViewById(R.id.delete_img);

        video_layout = itemView.findViewById(R.id.video_layout);
        image_layout = itemView.findViewById(R.id.image_layout);

        videoPath = itemView.findViewById(R.id.video_path);
        delete_video = itemView.findViewById(R.id.delete_video);
        voice_off = itemView.findViewById(R.id.ic_voice);
    }
}
