package com.damon.ropa.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ropa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText passowd,email;
    MaterialButton btn_close,btn_login;
    MaterialTextView create_acount;

    TextView errors;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Iniciando session espera...");
        progressDialog.setCanceledOnTouchOutside(false);

        passowd = findViewById(R.id.password_edit_text);
        email = findViewById(R.id.email);
        btn_login = findViewById(R.id.next_button);
        btn_close = findViewById(R.id.cancel_button);
        errors = findViewById(R.id.txt_erros);
        create_acount = findViewById(R.id.create_acount);


        btn_close.setOnClickListener(v -> finish());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passowd.getText().toString();
                String emai = email.getText().toString();
                if (TextUtils.isEmpty(pass) && TextUtils.isEmpty(emai)){
                    Toast.makeText(LoginActivity.this, "Por favor ingresa tus datos", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emai,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this,CreateActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            errors.setText(e.getMessage());
                        }
                    });
                }
            }
        });

        create_acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,CreateActivity.class);
            startActivity(intent);
            finish();
        }
    }
}