package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ropa.R;
import com.damon.ropa.adapters.ImagesProductsAdapter;
import com.damon.ropa.models.ImagesList;
import com.damon.ropa.models.ProductEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsProduct extends AppCompatActivity {

    ImageView image_product;

    TextView title,price,descripton,cantidad,category;

    List<ImagesList> imagesLists = new ArrayList<>();
    ImagesProductsAdapter imagesProductsAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_product);

        reference = FirebaseDatabase.getInstance().getReference().child("Ropa");

        category = findViewById(R.id.category);
        recyclerView = findViewById(R.id.img_reycler);
        recyclerView.setHasFixedSize(true);
        title = findViewById(R.id.titulo);
        price = findViewById(R.id.precio);
        descripton = findViewById(R.id.description);
        cantidad = findViewById(R.id.cantidad);


        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            ProductEntry productEntry =(ProductEntry) bundle.getSerializable("product");
            System.out.println(productEntry.url);
            title.setText(productEntry.getTitle());
            price.setText("$" + productEntry.getPrice());
            descripton.setText(productEntry.getDescription());
            cantidad.setText(""+productEntry.getCantidad());
            category.setText(productEntry.getCategory());
            getImages(productEntry.getCategory(),productEntry.getId());
            imagesProductsAdapter = new ImagesProductsAdapter(imagesLists,this);
            recyclerView.setAdapter(imagesProductsAdapter);
        }
    }
    void getImages(String ref,String id){
        reference.child(ref).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("url").getChildrenCount()>0){
                        for (DataSnapshot dataSnapshot : snapshot.child("url").getChildren()){
                            imagesLists.add(new ImagesList(dataSnapshot.getKey(),dataSnapshot.getValue().toString()));
                        }

                        imagesProductsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}