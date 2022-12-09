package com.example.myapplication.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.FileUtil;
import com.example.myapplication.models.Post;
import com.example.myapplication.providers.AuthProviders;
import com.example.myapplication.providers.ImageProviders;
import com.example.myapplication.providers.PostProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import javax.annotation.Nullable;

public class PostActivity extends AppCompatActivity {
    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    private final int Gallery_REQUEST_CODE=1;
    Button mbtnPost;
    ImageProviders mImageProvider;

    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    ImageView mImageViewCaribe;
    ImageView mImageViewAndina;
    ImageView mImageViewAmazonia;
    ImageView mImageViewInsular;
    ImageView mImageViewPacifica;
    ImageView mImageViewOrinoquia;
    TextView mTextViewCategoria;
    String mCategoria="";//Variable Global
    PostProvider mPostProvider;
    String mTitle="";//Variable Global
    String mDescription="";//Variable Global
    AuthProviders mAuthProviders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageViewPost1=findViewById(R.id.ImageViewPost1);
        mbtnPost=findViewById(R.id.btnPost);


        mTextInputTitle=findViewById(R.id.TextInputTitle);
        mTextInputDescription=findViewById(R.id.TextInputDescription);
        mImageViewCaribe=findViewById(R.id.ImageViewCaribe);
        mImageViewAndina=findViewById(R.id.ImageViewAndina);
        mImageViewAmazonia=findViewById(R.id.ImageViewAmazonia);
        mImageViewInsular=findViewById(R.id.ImageViewInsular);
        mImageViewPacifica=findViewById(R.id.ImageViewPacifica);
        mImageViewOrinoquia=findViewById(R.id.ImageViewOrinoquia);
        mTextViewCategoria=findViewById(R.id.TextViewCategoria);
        mImageProvider = new ImageProviders();
        mPostProvider= new PostProvider();
        mAuthProviders=new AuthProviders();





        mbtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveImage(); Antes
                ClickPost();
            }
        });
        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mImageViewCaribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoria= "CARIBE";
                mTextViewCategoria.setText(mCategoria);
            }
        });

        mImageViewAndina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoria= "ANDINA";
                mTextViewCategoria.setText(mCategoria);
            }
        });
        mImageViewAmazonia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoria= "AMAZONIA";
                mTextViewCategoria.setText(mCategoria);
            }
        });
        mImageViewInsular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoria= "INSULAR";
                mTextViewCategoria.setText(mCategoria);
            }
        });
        mImageViewPacifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCategoria= "PACIFICA";
                mTextViewCategoria.setText(mCategoria);
            }
        });
        mImageViewOrinoquia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoria= "ORINOQUIA";
                mTextViewCategoria.setText(mCategoria);
            }
        });




    }

    private void ClickPost() {
        mTitle=mTextInputTitle.getText().toString();
        mDescription=mTextInputDescription.getText().toString();
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategoria.isEmpty()){
            if(mImageFile !=null){
                saveImage();
            }else {
                Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImage() {
        mImageProvider.save(PostActivity.this,mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url=uri.toString();
                            Post post=new Post();
                            post.setImage1(url);
                            post.setTitle(mTitle);
                            post.setDescription(mDescription);
                            post.setCategory(mCategoria);
                            post.setIdUser(mAuthProviders.getUid());

                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> tasksave) {
                                    if(tasksave.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "La información se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PostActivity.this, "Error al almacenar al imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openGallery() {
        //este metodo permite abrir la galeria
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* VALIDACION DE IMAGEN CON GALERIA */
        if (requestCode == Gallery_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}