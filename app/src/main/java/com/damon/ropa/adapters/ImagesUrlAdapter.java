package com.damon.ropa.adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damon.ropa.R;
import com.damon.ropa.activitys.ImageViewerActivity;
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
                list.get(position).endsWith(".png") || list.get(position).contains(".png")){
            holder.image_layout.setVisibility(View.VISIBLE);
            Glide.with(activity).load(list.get(position)).into(holder.img_url);
            holder.delete_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.img_url.setOnClickListener(v -> {
                Intent intent  =new Intent(activity, ImageViewerActivity.class);
                intent.putExtra("url",list.get(position));
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.img_url, ViewCompat.getTransitionName(holder.img_url));
                activity.startActivity(intent,compat.toBundle());
            });
        }else if (list.get(position).endsWith(".mp4") || list.get(position).contains(".mp4")){
            holder.video_layout.setVisibility(View.VISIBLE);
//            holder.progressVideo.setVisibility(View.GONE);

            //SetVideo(holder, position);
            holder.setVideo(activity.getApplication(),list.get(position));
        }

    }

    private void SetVideo(@NonNull ImagesUrl holder, int position) {
        try {
            final MediaPlayer[] mediaPlayer = {null};
            holder.videoPath.setVideoPath(list.get(position));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                holder.videoPath.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == mp.MEDIA_INFO_BUFFERING_START){
                            holder.progressVideo.setVisibility(View.VISIBLE);
                        }else if (what == mp.MEDIA_INFO_BUFFERING_END){
                            holder.progressVideo.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
            }

            holder.videoPath.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer[0] = mp;
                    mp.setVolume(0, 0);
                    holder.progressVideo.setVisibility(View.GONE);
                    mp.start();
                    mp.setLooping(true);

                    holder.time_duration.setVisibility(View.VISIBLE);

                    holder.time_duration.setText(getTimeVideo(mp.getDuration()/1000));

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
                    try {
                        if (isOffAuido[0]) {
                            if (mediaPlayer[0] != null) {
                                mediaPlayer[0].setVolume(0.5f, 0.5f);
                            }
                            holder.voice_off.setImageResource(R.drawable.ic_voice_on);
                            isOffAuido[0] = false;
                        } else {
                            if (mediaPlayer[0] != null) {
                                mediaPlayer[0].setVolume(0, 0);
                            }
                            holder.voice_off.setImageResource(R.drawable.voice_off);
                            isOffAuido[0] = true;
                        }
                    }catch (IllegalStateException e){
                        e.printStackTrace();
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

    @Override
    public void onViewAttachedToWindow(@NonNull ImagesUrl holder) {
        super.onViewAttachedToWindow(holder);
        //Se llama cuando una vista creada por este adaptador se ha adjuntado a una ventana.
        System.out.println("onViewAttachedToWindow" + holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ImagesUrl holder) {
        super.onViewDetachedFromWindow(holder);
        //se llama cuando una vista creada por este adaptador se ha separado de su ventana.
        System.out.println("onViewDetachedFromWindow" + holder.getItemId());
    }

    @Override
    public void onViewRecycled(@NonNull ImagesUrl holder) {
        super.onViewRecycled(holder);
        //se llama cuando se recicla
        System.out.println("onViewRecycled" +holder);
    }

    private String getTimeVideo(int seconds){
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d",hr)+  ":" + String.format("%02d",mn)+ ":" + String.format("%02d",sec);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
