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
import com.google.firebase.auth.AuthResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    CircleImageView mCircleImageView;
    TextInputEditText mTextInputEditTextUsername;
    TextInputEditText mTextInputEditTextEmailRegister;
    TextInputEditText mTextInputEditTextPasswordRegister;
    TextInputEditText mTextInputEditTextConfirmPassword;
    Button mButtonRegister;
    //FirebaseAuth mAuth;
    AuthProviders mAuthProviders;
    //FirebaseFirestore mFirestore;
    UsersProviders mUsersproviders;
    AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instancias
        mCircleImageView = findViewById(R.id.CirleImageViewBack);
        mTextInputEditTextUsername = findViewById(R.id.TextInputEditTextUsername);
        mTextInputEditTextEmailRegister = findViewById(R.id.TextInputEditTextEmailRegister);
        mTextInputEditTextPasswordRegister = findViewById(R.id.TextInputEditTextPasswordRegister);
        mTextInputEditTextConfirmPassword = findViewById(R.id.TextInputEditTextConfirmPassword);
        mButtonRegister = findViewById(R.id.ButtonRegister);


        mAuthProviders =new AuthProviders();
        mUsersproviders=new UsersProviders();


        mDialog =new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();


        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Register() {
        String username = mTextInputEditTextUsername.getText().toString();
        String email = mTextInputEditTextEmailRegister.getText().toString();
        String password = mTextInputEditTextPasswordRegister.getText().toString();
        String ConfirmPassword = mTextInputEditTextConfirmPassword.getText().toString();


        // estas lineas de codigo le estoy indicando que se deben completar, username, email, password, confirmpassword, es decir que no deben ir vacios
        // el simbolo ! significa diferente de vacio
        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !ConfirmPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if(password.equals(ConfirmPassword)){/*siginifica si la contraseña es igual igual a contraseña equals, no se utiliza == por que en esta caso son objetos password y confirmpassword*/
                    if(password.length() >=6){
                        createUser(username,email,password);
                    }else {
                        Toast.makeText(this, "Las contraseñas debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Has insertado todos los campos, pero el correo no es valido", Toast.LENGTH_SHORT).show();
                // en el text va el mensaje emergente, y lo adecuado es que sea corto LENGH_SHORT, pero tambien se puede utilizar de manera  largo LENGTH_LONG
                // This hace referencia a que es esta clase, la clase register en esta caso
            }
        }
        else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();

        }
    }



    private void createUser(final String username,final  String email, String password) {
        mDialog.show();
        mAuthProviders.register(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String id=mAuthProviders.getUid();
                            User user=new User();
                            user.setId(id);
                            user.setEmail(email);
                            user.setUsername(username);
                            user.setPassword(password);
                            mUsersproviders.create(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mDialog.dismiss();
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this,"el usuario se almaceno correctamente",Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);

                                            }else {
                                                Toast.makeText(RegisterActivity.this,"no se puedo almacenar en la base de datos",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Toast.makeText(RegisterActivity.this, "El usuario se registro correctamente", Toast.LENGTH_SHORT).show();
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    // este metodo isEmailValid es el que me permite realizar las validaciones con caracteres especiales en el correo entre otras validaciones
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}






