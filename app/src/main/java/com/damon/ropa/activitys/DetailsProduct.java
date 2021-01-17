package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ropa.R;
import com.damon.ropa.adapters.ImagesProductsAdapter;
import com.damon.ropa.models.ImagesList;
import com.damon.ropa.models.ProductEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsProduct extends AppCompatActivity {

    ImageView edit_prod;

    TextView title,price,descripton,cantidad,category;

    List<ImagesList> imagesLists = new ArrayList<>();
    ImagesProductsAdapter imagesProductsAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference reference,userRef;

    ProductEntry productEntry;

    ImageView btn_instagram,btn_whatsapp,btn_telegram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_product);

        reference = FirebaseDatabase.getInstance().getReference().child("Ropa");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        edit_prod = findViewById(R.id.editar_prod);
        category = findViewById(R.id.category);
        recyclerView = findViewById(R.id.img_reycler);
        recyclerView.setHasFixedSize(true);
        title = findViewById(R.id.titulo);
        price = findViewById(R.id.precio);
        descripton = findViewById(R.id.description);
        cantidad = findViewById(R.id.cantidad);
        btn_instagram = findViewById(R.id.instagram);
        btn_whatsapp = findViewById(R.id.whats);
        btn_telegram = findViewById(R.id.telegram);


        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
             productEntry =(ProductEntry) bundle.getSerializable("product");
            System.out.println(productEntry.url);
            title.setText(productEntry.getTitle());
            price.setText("$" + productEntry.getPrice());
            descripton.setText(productEntry.getDescription());
            cantidad.setText(""+productEntry.getCantidad());
            category.setText(productEntry.getCategory());
            getImages(productEntry.getCategory(),productEntry.getId());
            imagesProductsAdapter = new ImagesProductsAdapter(imagesLists,this);
            recyclerView.setAdapter(imagesProductsAdapter);

            btn_instagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/variedadesjastoh/?igshid=v5r52ujztizn"));
                    startActivity(intent);
                }
            });

            btn_whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone=593984334637&text=Hola,%20me%20interesa%20tu%20producto%20"+productEntry.getTitle()+"%20"));
                    startActivity(intent);
                }
            });

            btn_telegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://telegram.me/JasminCevallos"));
                    final String appName = "org.telegram.messenger";
                    i.setPackage(appName);
                    startActivity(i);
                }
            });

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

                        productEntry.getUrl().clear();
                        productEntry.setUrl(imagesLists);

                        imagesProductsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth != null){

          userRef.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if (snapshot.exists()){
                      String rol = snapshot.child("rol").getValue().toString();


                      if (rol.equals("admin")){
                          edit_prod.setVisibility(View.VISIBLE);
                          edit_prod.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Intent intent  = new Intent(DetailsProduct.this,CreateActivity.class);
                                  Bundle bundle = new Bundle();
                                  bundle.putSerializable("product",productEntry);
                                  intent.putExtras(bundle);
                                  startActivity(intent);
                              }
                          });
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}