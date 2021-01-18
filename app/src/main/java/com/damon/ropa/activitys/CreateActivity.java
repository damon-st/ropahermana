package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ropa.R;
import com.damon.ropa.adapters.ImagesUrlAdapter;
import com.damon.ropa.models.CatgoriasM;
import com.damon.ropa.models.ImagesList;
import com.damon.ropa.models.ProductEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class CreateActivity extends AppCompatActivity {

    ImageView camera;
    Spinner categoriaSpinner;
    ArrayAdapter<String> adapter;
    private List<String> nomeConsulta = new ArrayList<String>();

    DatabaseReference refCategoria;

    RecyclerView imgRecycler;
    AppCompatEditText titulo, description, precio, cantidad, marca;
    List<String> url_list = new ArrayList<>();
    ImagesUrlAdapter imagesUrlAdapter;
    LinearLayoutManager linearLayoutManager;


    private Dialog dialogProgress;
    private TextView textoProgress;


    MaterialTextView title_update_create;

    DatabaseReference productoRef,updateRef;
    StorageReference firebaseStorage, filePath;

    MaterialButton btn_create,btn_delete;

    ProductEntry productEntry;
    List<String> idImage = new ArrayList<>();
    boolean isUpdate = false;
    String ventaRef;
    int cuentaFotosSubidas;
    String otroCategoria = "";

    String imgPortada = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
            }
        }

        categoriaSpinner = findViewById(R.id.categirias_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomeConsulta);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

        refCategoria = FirebaseDatabase.getInstance().getReference().child("Categorias");
        category = "Carteras";
        productoRef =  FirebaseDatabase.getInstance().getReference().child("Ropa");
        firebaseStorage = FirebaseStorage.getInstance().getReference().child("Products");
        dialogProgress = new Dialog(this);

        title_update_create = findViewById(R.id.titulo_edit_create);
        imgRecycler = findViewById(R.id.images_url);
        imgRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        imgRecycler.setLayoutManager(linearLayoutManager);
        imagesUrlAdapter = new ImagesUrlAdapter(url_list,this);
        imgRecycler.setAdapter(imagesUrlAdapter);

        btn_create =findViewById(R.id.crear_prod);
        camera =  findViewById(R.id.camera);
        titulo = findViewById(R.id.titulo_prod);
        description =findViewById(R.id.des_prod);
        precio =findViewById(R.id.precio_prod);
        cantidad = findViewById(R.id.cant_prod);
        marca =findViewById(R.id.marca_prod);
        btn_delete = findViewById(R.id.delete_prod);

        getSpinnerInfo();

        camera.setOnClickListener(v -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this));










        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle!=null){
            isUpdate = true;
            title_update_create.setText("ACTUALIZAR PRODUCTO");
            btn_create.setText("Editar Producto");
            productEntry = (ProductEntry) bundle.getSerializable("product");

            ventaRef = productEntry.getId();

            System.out.println("Titulo"+productEntry.getTitle());
            titulo.setText(productEntry.getTitle());
            description.setText(productEntry.getDescription());
            precio.setText(""+productEntry.getPrice());
            cantidad.setText(""+productEntry.getCantidad());
            marca.setText(productEntry.getMarca());
            category = productEntry.getCategory();
            otroCategoria = productEntry.getCategory();
            imgPortada = productEntry.getImgPortada();
            cuentaFotosSubidas = productEntry.getUrl().size();

            for (ImagesList imagesList : productEntry.getUrl()){
                url_list.add(imagesList.getUrl());
                listaNueva.add(imagesList.getUrl());
                mediaIdList.add(imagesList.getId());
                imagesUrlAdapter.notifyDataSetChanged();
            }

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteProduct(ventaRef,productEntry.getUrl());
                }
            });
        }

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpdate){
                    saveVenta();
                }else {
                    UpdateVenta();
                }
            }
        });

        categoriaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!otroCategoria.equals("")){
                    if (otroCategoria.equals(nomeConsulta.get(position))){
                        category = nomeConsulta.get(position);
                    }
                }else {
                    category = nomeConsulta.get(position);
                    System.out.println(category);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void deleteProduct(String id, List<ImagesList> url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (ImagesList imagesList : url){
            storage.getReferenceFromUrl(imagesList.getUrl()).delete();
        }
        productoRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(CreateActivity.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void UpdateVenta(){
        title = titulo.getText().toString().toLowerCase();
        descripcion = description.getText().toString();
        String prices = precio.getText().toString();
        String cantidads = cantidad.getText().toString();

        if (title.equals("") || descripcion.length()==0 ||TextUtils.isEmpty(prices) || TextUtils.isEmpty(cantidads) ||
                TextUtils.isEmpty(marca.getText().toString())
        ){
            Toast.makeText(this, "LLena los campos porfavor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            price = Double.parseDouble(prices);
            cantidadProducto = Integer.parseInt(cantidads);
            marcaProd = marca.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ShowProgress();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("title", title);
        hashMap.put("description", descripcion);
        hashMap.put("cantidad", cantidadProducto);
        hashMap.put("category", category);
        hashMap.put("marca", marcaProd);
        hashMap.put("price", price);
        hashMap.put("imgPortada",imgPortada);


        if (url_list.size() > cuentaFotosSubidas) {
            Toast.makeText(this, "Entro ala subida de imagenes", Toast.LENGTH_SHORT).show();
            for(int i = cuentaFotosSubidas; i <url_list.size(); i++) {
                final int finalI = i;

                System.out.println("uris " + url_list.get(i));
                String mediaId = productoRef.child("url").push().getKey();

                mediaIdList.add(mediaId);



                String addChild = UUID.randomUUID().toString();
                filePath = firebaseStorage.child(addChild + "." + "png");

                System.out.println("filepath" + filePath);
//        final File file = new File(SiliCompressor.with(CrearVentaActivity.this)
//        .compress(FileUtils.getPath(CrearVentaActivity.this,imgURI),
//                new File(CrearVentaActivity.this.getCacheDir(),"temp")));
//
//        Uri uri = Uri.fromFile(file);

                File tumb_filePath = new File(url_list.get(i));


                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(90)
                            .compressToBitmap(tumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                if (textoProgress != null) textoProgress.setText("Imagenes por subir " + i);

                firebaseStorage.child(addChild + "." + "png").putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            firebaseStorage.child(addChild + "." + "png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    System.out.println(task.getResult().toString());
                                    listaNueva.add(task.getResult().toString().toString());



                                    totalMEdia++;
                                    if (textoProgress !=null) textoProgress.setText("Imagenes subidas " + totalMEdia );

                                    if (listaNueva.size() == url_list.size())
                                        updateVenta(hashMap);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressDialog.dismiss();
                        if (textoProgress != null) textoProgress.setText("Error " + e.getMessage());
                        dialogProgress.setCanceledOnTouchOutside(true);
                        dialogProgress.setCancelable(true);
                    }
                });
            }

        }else {
            Toast.makeText(this, "Entro sin subida de iamgenes", Toast.LENGTH_SHORT).show();

            productoRef.child(ventaRef).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialogProgress.cancel();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (textoProgress != null) textoProgress.setText("Error " + e.getMessage());
                    dialogProgress.setCanceledOnTouchOutside(true);
                    dialogProgress.setCancelable(true);
                }
            });
        }



    }

    private void updateVenta(HashMap<String, Object> hashMap) {
        System.out.println("lista nueva "+listaNueva);
        for (int  i= 0; i < listaNueva.size(); i++ ){
            hashMap.put("/url/" + mediaIdList.get(i) + "/",  listaNueva.get(i));
        }

        mediaIdList.clear();
        url_list.clear();
        totalMEdia = 0;

        productoRef.child(ventaRef).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialogProgress.cancel();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (textoProgress != null) textoProgress.setText("Error " + e.getMessage());
                dialogProgress.setCanceledOnTouchOutside(true);
                dialogProgress.setCancelable(true);
            }
        });
    }


    private void getSpinnerInfo() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                refCategoria.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            CatgoriasM catgoriasM = dataSnapshot.getValue(CatgoriasM.class);
                            nomeConsulta.add(catgoriasM.getId());
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri url = result.getUri();
            System.out.println(url);
            url_list.add(url.getPath());
            imagesUrlAdapter.notifyDataSetChanged();
        }
    }


    int totalMEdia = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    ArrayList<String> listaNueva = new ArrayList<>();

    Bitmap bitmap = null;

    String title,descripcion,marcaProd,category;

    double price =0 ;
    int cantidadProducto = 0;

    private void saveVenta(){

         title = titulo.getText().toString().toLowerCase();
        descripcion = description.getText().toString();
        String prices = precio.getText().toString();
        String cantidads = cantidad.getText().toString();

        if (title.equals("") || descripcion.length()==0 ||TextUtils.isEmpty(prices) || TextUtils.isEmpty(cantidads) ||
                TextUtils.isEmpty(marca.getText().toString())
            ){
            Toast.makeText(this, "LLena los campos porfavor", Toast.LENGTH_SHORT).show();
            return;
        }else {


            try {
                price = Double.parseDouble(prices);
                cantidadProducto = Integer.parseInt(cantidads);
                marcaProd = marca.getText().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Title" + title);
            System.out.println("Descripcion" + descripcion);
            System.out.println("price" + price);
            System.out.println("catidadProducto" + cantidadProducto);
            System.out.println("marcaprod" + marcaProd);
            System.out.println("category" + category);

        ShowProgress();

        HashMap<String, Object> hashMap = new HashMap<>();


        String ventaRef = productoRef.push().getKey();


        if (url_list.size() > 0) {
            for(int i = 0; i <url_list.size(); i++) {
                final int finalI = i;

                System.out.println("uris " + url_list.get(i));
                String mediaId = productoRef.child("url").push().getKey();

                mediaIdList.add(mediaId);



                String addChild = UUID.randomUUID().toString();
                filePath = firebaseStorage.child(addChild + "." + "png");

                System.out.println("filepath" + filePath);
//        final File file = new File(SiliCompressor.with(CrearVentaActivity.this)
//        .compress(FileUtils.getPath(CrearVentaActivity.this,imgURI),
//                new File(CrearVentaActivity.this.getCacheDir(),"temp")));
//
//        Uri uri = Uri.fromFile(file);

                File tumb_filePath = new File(url_list.get(i));


                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(90)
                            .compressToBitmap(tumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                if (textoProgress != null) textoProgress.setText("Imagenes por subir " + i);

                firebaseStorage.child(addChild + "." + "png").putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            firebaseStorage.child(addChild + "." + "png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    System.out.println(task.getResult().toString());
                                    listaNueva.add(task.getResult().toString().toString());



                                    totalMEdia++;
                                    if (textoProgress !=null) textoProgress.setText("Imagenes subidas " + totalMEdia );

                                    imgPortada = listaNueva.get(0);
                                    if (listaNueva.size() == url_list.size())
                                        updateVentaDataBase(ventaRef,hashMap);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressDialog.dismiss();
                        if (textoProgress != null) textoProgress.setText("Error " + e.getMessage());
                        dialogProgress.setCanceledOnTouchOutside(true);
                        dialogProgress.setCancelable(true);
                    }
                });
            }

        }else {
            hashMap.put("imgPortada","https://firebasestorage.googleapis.com/v0/b/ventadiamantes-329aa.appspot.com/o/Facturas%2Fae11c5a5-10ad-4c31-ad66-c1478032ad80.jpg?alt=media&token=a5dbc974-eec9-4653-a0f2-cf83ec744691");
            hashMap.put("url", "https://firebasestorage.googleapis.com/v0/b/ventadiamantes-329aa.appspot.com/o/Facturas%2Fae11c5a5-10ad-4c31-ad66-c1478032ad80.jpg?alt=media&token=a5dbc974-eec9-4653-a0f2-cf83ec744691");

            updateVentaDataBase(ventaRef,hashMap);
        }
        }

    }

    private void updateVentaDataBase(String ref, HashMap<String, Object> hashMap){

        System.out.println("lista nueva "+listaNueva);
        for (int  i= 0; i < listaNueva.size(); i++ ){
            hashMap.put("/url/" + mediaIdList.get(i) + "/",  listaNueva.get(i));
        }

        mediaIdList.clear();
        url_list.clear();
        totalMEdia = 0;

        hashMap.put("id", ref);
        hashMap.put("title", title);
        hashMap.put("description", descripcion);
        hashMap.put("cantidad", cantidadProducto);
        hashMap.put("category", category);
        hashMap.put("marca", marcaProd);
        hashMap.put("price", price);
        hashMap.put("imgPortada", imgPortada);


        productoRef.child(ref).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // progressDialog.dismiss();
                    dialogProgress.cancel();

                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  progressDialog.dismiss();
                if (textoProgress != null) textoProgress.setText("Error " + e.getMessage());
                dialogProgress.setCanceledOnTouchOutside(true);
                dialogProgress.setCancelable(true);
            }
        });

    }

    private void ShowProgress(){
        dialogProgress.setContentView(R.layout.progress_dialog);
        dialogProgress.setCanceledOnTouchOutside(false);
        dialogProgress.setCancelable(false);

        textoProgress = dialogProgress.findViewById(R.id.texto_progress);

        dialogProgress.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}