package com.damon.ropa.holder;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ropa.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.video.VideoListener;

public class ImagesUrl  extends RecyclerView.ViewHolder {

   public ImageView img_url,delete_img,delete_video,voice_off;
   public FrameLayout video_layout, image_layout;
   public VideoView videoPath;
   public TextView time_duration;
   public ProgressBar progressVideo;

   public PlayerView playerView;
    SimpleExoPlayer exoPlayer;

    private PlaybackStateListener playbackStateListener;


    public ImagesUrl(@NonNull View itemView) {
        super(itemView);
        img_url = itemView.findViewById(R.id.img_url);
        delete_img = itemView.findViewById(R.id.delete_img);

        video_layout = itemView.findViewById(R.id.video_layout);
        image_layout = itemView.findViewById(R.id.image_layout);

        videoPath = itemView.findViewById(R.id.video_path);
        delete_video = itemView.findViewById(R.id.delete_video);
        voice_off = itemView.findViewById(R.id.ic_voice);
        time_duration = itemView.findViewById(R.id.time_video);


        progressVideo = itemView.findViewById(R.id.progress_video);

        playerView = itemView.findViewById(R.id.ep_video_view);

        playbackStateListener = new PlaybackStateListener();
    }

    public void setVideo(final Application ctx, final String url) {

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(ctx).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(ctx);
            Uri video = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            if (url.contains("https")){
                exoPlayer.prepare(mediaSource);
            }else {
                ExtractorMediaSource audioSource = new ExtractorMediaSource(
                        Uri.parse(url),
                        new DefaultDataSourceFactory(ctx.getApplicationContext(),"MyExoplayer"),
                        new DefaultExtractorsFactory(),
                        null,
                        null
                );
                exoPlayer.prepare(audioSource);
            }

            exoPlayer.addListener(playbackStateListener);
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.setVolume(0);

            final boolean[] volumen = {true};

            voice_off.setOnClickListener(v -> {
                if (volumen[0]){
                    volumen[0] = false;
                    voice_off.setImageResource(R.drawable.ic_voice_on);
                    exoPlayer.setVolume(1);
                }else {
                    volumen[0] = true;
                    voice_off.setImageResource(R.drawable.voice_off);
                    exoPlayer.setVolume(0);
                }
            });


        } catch (Exception e) {
            Log.e("ViewHolder2", "exoplayer error" + e.toString());
        }


    }



    private class  PlaybackStateListener implements  Player.EventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    progressVideo.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    progressVideo.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    progressVideo.setVisibility(View.GONE);
                    voice_off.setVisibility(View.VISIBLE);

                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    progressVideo.setVisibility(View.GONE);
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
        }
    }

}
