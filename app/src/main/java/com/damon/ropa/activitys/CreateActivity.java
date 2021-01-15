package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
    TextInputEditText titulo, description, precio, cantidad, marca;
    List<String> url_list = new ArrayList<>();
    ImagesUrlAdapter imagesUrlAdapter;
    LinearLayoutManager linearLayoutManager;


    private Dialog dialogProgress;
    private TextView textoProgress;

    DatabaseReference productoRef;
    StorageReference firebaseStorage, filePath;

    MaterialButton btn_create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        categoriaSpinner = findViewById(R.id.categirias_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomeConsulta);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

        refCategoria = FirebaseDatabase.getInstance().getReference().child("Categorias");
        productoRef =  FirebaseDatabase.getInstance().getReference("Ropa");
        firebaseStorage = FirebaseStorage.getInstance().getReference().child("Products");
        dialogProgress = new Dialog(this);

        imgRecycler = findViewById(R.id.images_url);
        imgRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        imgRecycler.setLayoutManager(linearLayoutManager);
        imagesUrlAdapter = new ImagesUrlAdapter(url_list,this);
        imgRecycler.setAdapter(imagesUrlAdapter);

        btn_create = findViewById(R.id.crear_prod);
        camera = findViewById(R.id.camera);
        titulo = findViewById(R.id.titulo_prod);
        description = findViewById(R.id.des_prod);
        precio = findViewById(R.id.precio_prod);
        cantidad = findViewById(R.id.cant_prod);
        marca = findViewById(R.id.marca_prod);

        getSpinnerInfo();

        camera.setOnClickListener(v -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this));



        if (nomeConsulta.size()>0)
            category = nomeConsulta.get(0);


        categoriaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               category = nomeConsulta.get(position);
                System.out.println(category);
                productoRef =  FirebaseDatabase.getInstance().getReference("Ropa").child(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_create.setOnClickListener(v -> saveVenta());

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

         title = titulo.getText().toString();
        descripcion = description.getText().toString();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(descripcion)){
            Toast.makeText(this, "LLena los campos porfavor", Toast.LENGTH_SHORT).show();
            return;
        }

        price = Double.parseDouble(precio.getText().toString());
        cantidadProducto = Integer.parseInt(cantidad.getText().toString());
        marcaProd = marca.getText().toString();

        ShowProgress();

        HashMap<String, Object> hashMap = new HashMap<>();

        String ref = productoRef.push().getKey();

        if (url_list.size() > 0) {
            for(int i = 0; i <url_list.size(); i++) {
                final int finalI = i;

                System.out.println("uris " + url_list.get(i));
                String mediaId = productoRef.child(category).child("url").push().getKey();

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

                                    if (listaNueva.size() == url_list.size())
                                        updateVentaDataBase(ref,hashMap);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressDialog.dismiss();
                        dialogProgress.cancel();
                    }
                });
            }

        }else {
            hashMap.put("image", "https://firebasestorage.googleapis.com/v0/b/ventadiamantes-329aa.appspot.com/o/Facturas%2Fae11c5a5-10ad-4c31-ad66-c1478032ad80.jpg?alt=media&token=a5dbc974-eec9-4653-a0f2-cf83ec744691");

            updateVentaDataBase(ref,hashMap);
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
        hashMap.put("marca", marca);


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
                dialogProgress.cancel();
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
}