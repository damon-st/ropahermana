package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
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
import com.damon.cortarvideo.utils.FileUtils;
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

    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    ImageView camera,video_search;
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

    String imgPortada = "https://firebasestorage.googleapis.com/v0/b/variedadesjastho.appspot.com/o/jastho.jpeg?alt=media&token=e19dcba0-01c5-4fec-ab05-dfe3abaa7248";
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

        video_search = findViewById(R.id.video_search);
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


        video_search.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            }else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent.createChooser(intent,"Buscar Video"),400);
            }
        });








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

                category = nomeConsulta.get(position);
                System.out.println(category);


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

        if (title.equals("") || descripcion.length()==0 ||TextUtils.isEmpty(prices) || TextUtils.isEmpty(cantidads)){
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

                if (url_list.get(i).endsWith(".jpeg")||url_list.get(i).endsWith(".jpg")||
                   url_list.get(i).endsWith(".png")){

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

                }else if (url_list.get(i).endsWith(".mp4")){


                    filePath = firebaseStorage.child(addChild + "." + "mp4");

                    System.out.println("filepath" + filePath);



                    if (textoProgress != null) textoProgress.setText("Video por subir " + i);

                    firebaseStorage.child(addChild + "." + "mp4").putFile(Uri.parse(url_list.get(i))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                firebaseStorage.child(addChild + "." + "mp4").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        System.out.println(task.getResult().toString());
                                        listaNueva.add(task.getResult().toString().toString());



                                        totalMEdia++;
                                        if (textoProgress !=null) textoProgress.setText("Video subidos " + totalMEdia );

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
        }else if (requestCode == 400 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Intent intent = new Intent(CreateActivity.this,TrimmingVideo.class);
            intent.putExtra("video_path", FileUtils.getPath(this,uri));
            intent.putExtra("duration",getMediaDuration(uri));
            startActivityForResult(intent,200);
        }else if (requestCode == 200 ){
            if ( resultCode == RESULT_OK){
                String path = data.getStringExtra("path");
                url_list.add(path);
                imagesUrlAdapter.notifyDataSetChanged();
            }
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

        if (title.equals("") || descripcion.length()==0 ||TextUtils.isEmpty(prices) || TextUtils.isEmpty(cantidads))
            {
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

                if (url_list.get(i).endsWith(".jpeg")||url_list.get(i).endsWith(".jpg")||
                        url_list.get(i).endsWith(".png")) {

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


                }else if (url_list.get(i).endsWith(".mp4")){


                    filePath = firebaseStorage.child(addChild + "." + "mp4");

                    System.out.println("filepath" + filePath);
//        final File file = new File(SiliCompressor.with(CrearVentaActivity.this)
//        .compress(FileUtils.getPath(CrearVentaActivity.this,imgURI),
//                new File(CrearVentaActivity.this.getCacheDir(),"temp")));
//
//        Uri uri = Uri.fromFile(file);



                    if (textoProgress != null) textoProgress.setText("Video por subir " + i);

                    firebaseStorage.child(addChild + "." + "mp4").putFile(Uri.parse(url_list.get(i))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                firebaseStorage.child(addChild + "." + "mp4").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        System.out.println(task.getResult().toString());
                                        listaNueva.add(task.getResult().toString().toString());



                                        totalMEdia++;
                                        if (textoProgress !=null) textoProgress.setText("Video subidos " + totalMEdia );

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


            }

        }else {
            hashMap.put("imgPortada",imgPortada);
            hashMap.put("url", imgPortada);

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

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso Necesario");
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
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

    private int  getMediaDuration(Uri uriOfFile)  {
        MediaPlayer mp = MediaPlayer.create(this,uriOfFile);
        int duration = mp.getDuration();
        return  duration;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Gracias aora ya puedes  e enviar los videos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Por favor conseda los permisos", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }
}