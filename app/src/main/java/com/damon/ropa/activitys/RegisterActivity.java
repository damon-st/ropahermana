package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ropa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText email,password,name;
    MaterialButton btn_register,btn_cancel;
    TextView erros;

    DatabaseReference reference;

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = new ProgressDialog(this);

        progressBar.setTitle("Creando cuenta espera....");
        progressBar.setCanceledOnTouchOutside(false);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        name = findViewById(R.id.nombre);
        email =findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);
        erros = findViewById(R.id.txt_erros);

        btn_cancel = findViewById(R.id.cancel_button);
        btn_register = findViewById(R.id.register_button);

        btn_cancel.setOnClickListener(v -> finish());

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String emai = email.getText().toString();
                String names = name.getText().toString();

                if (TextUtils.isEmpty(pass) && TextUtils.isEmpty(emai)&& TextUtils.isEmpty(names)){
                    Toast.makeText(RegisterActivity.this, "Porfavor completa todos los campos ", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.show();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emai,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String uid = task.getResult().getUser().getUid();
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("names",names);

                                reference.child(uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Creado exitoso ingresa ahora", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        erros.setText(e.getMessage());
                                        progressBar.dismiss();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            erros.setText(e.getMessage());
                            progressBar.dismiss();
                        }
                    });
                }
            }
        });
    }
}