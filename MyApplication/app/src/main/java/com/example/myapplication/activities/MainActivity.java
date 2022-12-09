package com.example.myapplication.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.providers.UsersProviders;
import com.example.myapplication.providers.AuthProviders;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {
    TextView mTextViewRegister;
    TextInputEditText mTextInputEditTextEmail;
    TextInputEditText mTextInputEditTextPassword;
    Button mButtonLogin;
    SignInButton mButtonGoogle;
    AuthProviders mAuthProviders;
    private GoogleSignInClient mGoogleSignInClient;
    private final int REQUEST_CODE_GOOGLE=1;
    UsersProviders mUsersproviders;
    AlertDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //instancias
        mTextViewRegister=findViewById(R.id.TextViewRegister);
        mTextInputEditTextEmail=findViewById(R.id.TextInputEditTextEmail);
        mTextInputEditTextPassword=findViewById(R.id.TextInputEditTextPassword);
        mButtonLogin=findViewById(R.id.ButtonLogin);
        mButtonGoogle=findViewById(R.id.ButtonLoginSingInGoogle);


        mAuthProviders =new AuthProviders();
        mUsersproviders=new UsersProviders();



        mDialog =new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                 .build();


        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);





        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login ();


            }
        });
}



    private void signInGoogle () {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERROR", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        mDialog.show();
        mAuthProviders.googleLogin(account)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String id=mAuthProviders.getUid();
                            checkUserExist(id);
                            // Sign in success, update UI with the signed-in user's information


                        } else {
                            mDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("ERROR", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }


    private void checkUserExist(final String id) {

        mUsersproviders.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mDialog.dismiss();
                if(documentSnapshot.exists()){
                    Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    String email=mAuthProviders.getEmail();
                    User user=new User();
                    user.setEmail(email);
                    user.setId(id);
                    //Map<String, Object> map=new HashMap<>();
                    //map.put("email", email);
                    mUsersproviders.create(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Intent intent=new Intent( MainActivity.this,CompleteProfileActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this ,"No se pudo almacenar el usuario",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });

 }


    private void login() {
        String email=mTextInputEditTextEmail.getText().toString();
        String password=mTextInputEditTextPassword.getText().toString();
        mDialog.show();
        mAuthProviders.login(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent =new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }else {
                            Toast.makeText(MainActivity.this, "El email y contraseña no son correctas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Log.d("campo","email"+email);/*esta estructura del log.d me sirve para hacer pruebas*/
        Log.d("campo","password"+password);/*por consola de verificacion si la caja de texto de correo electronico del activity_main.xml esta enviando datos al login*/
    }
}



