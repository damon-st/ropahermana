package com.damon.ropa.adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        holder.image_layout.setVisibility(View.GONE);
        holder.video_layout.setVisibility(View.GONE);
        if (list.get(position).endsWith(".jpg")|| list.get(position).endsWith(".jpeg") |
                list.get(position).endsWith(".png")){
            holder.image_layout.setVisibility(View.VISIBLE);
            Glide.with(activity).load(list.get(position)).into(holder.img_url);
            holder.delete_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });
        }else if (list.get(position).endsWith(".mp4")){
            holder.video_layout.setVisibility(View.VISIBLE);

            try {
                final MediaPlayer[] mediaPlayer = {null};
                holder.videoPath.setVideoPath(list.get(position));

                holder.videoPath.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer[position] = mp;
                        mp.setVolume(0, 0);
                        mp.start();
                        mp.setLooping(true);

                        float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                        float screenRatio = holder.videoPath.getWidth() / (float) holder.videoPath.getHeight();

                        float scale = videoRatio / screenRatio;
                        if (scale >= 1f) {
                            holder.videoPath.setScaleX(scale);
                        } else {
                            holder.videoPath.setY(1f / scale);
                        }
                    }
                });

                final boolean[] isOffAuido = {true};
                holder.voice_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOffAuido[position]) {
                            if (mediaPlayer[position] != null) {
                                mediaPlayer[position].setVolume(0.5f, 0.5f);
                            }
                            holder.voice_off.setImageResource(R.drawable.ic_voice_on);
                            isOffAuido[position] = false;
                        } else {
                            if (mediaPlayer[position] != null) {
                                mediaPlayer[position].setVolume(0, 0);
                            }
                            holder.voice_off.setImageResource(R.drawable.voice_off);
                            isOffAuido[position] = true;
                        }

                    }
                });

                holder.delete_video.setOnClickListener(v -> {
                    list.remove(position);
                    notifyDataSetChanged();
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
