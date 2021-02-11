package com.damon.ropa.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.damon.ropa.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {


    ImageView imageView;
    String url;

    PhotoViewAttacher photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.img_viewer);
      //  imageView.setZoomable(true);

//        getActionBar().hide();

        Intent intent = getIntent();
        try {
            photoView = new PhotoViewAttacher(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (intent !=null && intent.getExtras() !=null){
            url = intent.getStringExtra("url");
            //Picasso.get().load(url).into(imageView);
            Glide.with(this).load(url).into(imageView);
        }
    }
}