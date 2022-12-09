package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.providers.AuthProviders;
import com.example.myapplication.providers.UsersProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import dmax.dialog.SpotsDialog;


public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputEditTextUsernameProfile;
    Button mButtonCompleteProfile;
    //FirebaseAuth mAuth;
    AuthProviders mAuthProviders;
    //FirebaseFirestore mFirestore;
    UsersProviders mUsersproviders;
    AlertDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputEditTextUsernameProfile=findViewById(R.id.TextInputEditTextUsernameProfile);
        mButtonCompleteProfile=findViewById(R.id.ButtonCompleteProfile);



        mAuthProviders =new AuthProviders();
        mUsersproviders=new UsersProviders();

        mDialog =new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        mButtonCompleteProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                register();
            }
        });
    }
    private void register() {
        String username=mTextInputEditTextUsernameProfile.getText().toString();

        if(!username.isEmpty()){
            updateUser(username);
        }else {
            Toast.makeText(this, "para continuar insterta el nombre del usuario", Toast.LENGTH_SHORT).show();

        }

    }

    private void updateUser(final String username) {
        String id=mAuthProviders.getUid();
        User user = new User();
        user.setUsername( username);
        user.setId(id);
        mDialog.show();

        mUsersproviders.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(CompleteProfileActivity.this, "No se almaceno el usuario en la base de datos", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}