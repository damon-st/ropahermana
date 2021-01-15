package com.damon.ropa.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ropa.R;
import com.damon.ropa.models.ProductEntry;
import com.squareup.picasso.Picasso;

public class DetailsProduct extends AppCompatActivity {

    ImageView image_product;

    TextView title,price,descripton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_product);


        title = findViewById(R.id.titulo);
        price = findViewById(R.id.precio);
        descripton = findViewById(R.id.description);

        image_product = findViewById(R.id.image_product);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            ProductEntry productEntry =(ProductEntry) bundle.getSerializable("product");
            System.out.println(productEntry.url);
            Picasso.get().load(productEntry.getUrl()).into(image_product);
            title.setText(productEntry.getTitle());
            price.setText("$" + productEntry.getPrice());
            descripton.setText(productEntry.getDescription());
        }
    }
}