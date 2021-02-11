package com.damon.ropa.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.damon.cortarvideo.HgLVideoTrimmer;
import com.damon.cortarvideo.interfaces.OnHgLVideoListener;
import com.damon.cortarvideo.interfaces.OnTrimVideoListener;
import com.damon.ropa.R;


import java.io.File;

public class TrimmingVideo extends AppCompatActivity  implements OnTrimVideoListener, OnHgLVideoListener {


    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;

    private String path;
    private int maxDuration = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimming_video);

        path = getIntent().getStringExtra("video_path");
        maxDuration = getIntent().getIntExtra("duration",10);


        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Preparando para enviar...");

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {


            /**
             * get total duration of video file
             */
            Log.e("tg", "maxDuration = " + maxDuration);
            //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                 Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTrimStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void getResult(Uri uri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, contentUri.getPath()), Toast.LENGTH_SHORT).show();

            }
        });

        try {

            String path = uri.getPath();
            File file = new File(path);
            System.out.println(file.length());
            System.out.println(uri);
            Log.e("tg", " path1 = " + path + " uri1 = " + Uri.fromFile(file));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//            intent.setDataAndType(Uri.fromFile(file), "video/*");
//            startActivity(intent);
//            finish();
            int s = Math.round(file.length());
            int da = s / (1024 * 1024);
            if (da <= 10){
                sendVideo(Uri.fromFile(file));
            }else {
                mProgressDialog.cancel();
                mVideoTrimmer.destroy();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TrimmingVideo.this, "Lo Sentimos porfavor el Peso Maximo del Video 10MB ", Toast.LENGTH_LONG).show();
                    }
                });
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendVideo(Uri fromFile) {
        Intent intent = getIntent();
        intent.putExtra("path",fromFile.toString());
        setResult(Activity.RESULT_OK,intent);
        onBackPressed();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmingVideo.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}