package com.example.myapplication.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {
    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    private final int Gallery_REQUEST_CODE=1;
    private final int Gallery_REQUEST_CODE2=2;
    private final int PHOTO_REQUEST_CODE=3;
    private final int PHOTO_REQUEST_CODE2=4;
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
    AlertDialog mDialog;
    CircleImageView mcircleImageViewPost; //Es el boton para dar atras
    AlertDialog.Builder mBuilderSelector; //Se encarga de hacer un menu pequeño, para elegir entre la camara y la galeria
    CharSequence options[]; //permite crear el areglo de dos opciones


    //Se utilizan para camara
    String mAbsolutePhotoPatch;
    String mPhotoPatch;
    File mPhotoFile;

    //Fotografia 2
    String mAbsolutePhotoPatch2;
    String mPhotoPatch2;
    File mPhotoFile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageViewPost1=findViewById(R.id.ImageViewPost1);
        mImageViewPost2=findViewById(R.id.ImageViewPost2);
        mbtnPost=findViewById(R.id.btnPost);
        mcircleImageViewPost=findViewById(R.id.CirleImageViewBackPost);


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



        mDialog =new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();


        mBuilderSelector=new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecione una opcion");
        options= new CharSequence[]{"Imagen De Galeria", "Tomar Fotografia"};

        mcircleImageViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(PostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


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
                selectOptionImage(1);
               // openGallery(Gallery_REQUEST_CODE);
            }
        });
        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
                //openGallery(Gallery_REQUEST_CODE2);
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
//el DialogInterface es un evento donde el usuario tiene que elegir entre las opciones dando click
    private void selectOptionImage(int numberImage) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which ==0){
                    if(numberImage==1){
                        openGallery(Gallery_REQUEST_CODE);
                    }else if(numberImage==2){
                        openGallery(Gallery_REQUEST_CODE2);
                    }
                }
                else if (which==1){
                    if (numberImage==1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    }else if(numberImage==2) {
                        takePhoto(PHOTO_REQUEST_CODE2);
                    }

                }

            }
        });
        mBuilderSelector.show();
    }

    private void takePhoto(int requestCode) {
       // Toast.makeText(this, "Tomar Foto", Toast.LENGTH_SHORT).show();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try {
                photoFile= createPhotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.myapplication",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }

        }

    }

    private File createPhotoFile(int requestCode) throws IOException {

        File storageDir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
              new Date()+"_photo",
              ".jpg",
              storageDir
        );
        if (requestCode==PHOTO_REQUEST_CODE){
            mPhotoPatch="File:"+ photoFile.getAbsolutePath();
            mAbsolutePhotoPatch= photoFile.getAbsolutePath();
        }else if (requestCode==PHOTO_REQUEST_CODE2){
            mPhotoPatch2="File:"+ photoFile.getAbsolutePath();
            mAbsolutePhotoPatch2= photoFile.getAbsolutePath();
        }

        return photoFile;

    }

    private void ClickPost() {
        mTitle=mTextInputTitle.getText().toString();
        mDescription=mTextInputDescription.getText().toString();
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategoria.isEmpty()){
            //Ambas IMG de galeria
            if(mImageFile !=null && mImageFile2 !=null){
                saveImage(mImageFile,mImageFile2);
                //Ambas IMG de camara
            }else if (mPhotoFile !=null && mPhotoFile2 !=null){
                saveImage(mPhotoFile,mPhotoFile2);
                //una IMG de galeria y una IMG de camara
            }else if (mImageFile !=null && mPhotoFile !=null){
                saveImage(mImageFile,mPhotoFile);
            }else if (mPhotoFile2 !=null && mImageFile2 !=null){
                saveImage(mPhotoFile2,mImageFile2);
            }
            else {
                Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImage( File mImageFile, File mImageFile2) {
        mDialog.show();
        mImageProvider.save(PostActivity.this,mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url=uri.toString();


                            mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                               String url2=uri2.toString();
                                                Post post=new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle);
                                                post.setDescription(mDescription);
                                                post.setCategory(mCategoria);
                                                post.setIdUser(mAuthProviders.getUid());

                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> tasksave) {
                                                        mDialog.dismiss();
                                                        if(tasksave.isSuccessful()){
                                                            clearForm();
                                                            Toast.makeText(PostActivity.this, "La información se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }else{
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this, "Error al almacenar la imagen 2", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });




                        }
                    });
                    Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Error al almacenar al imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void clearForm() {

        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextViewCategoria.setText("");
        mImageViewPost1.setImageResource(R.drawable.agregarfoto);
        mImageViewPost2.setImageResource(R.drawable.agregarfoto);
        mTitle="";
        mDescription="";
        mCategoria="";
        mImageViewPost1=null;
        mImageViewPost2=null;
    }

    private void openGallery(int requestCode) {
        //este metodo permite abrir la galeria
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* VALIDACION DE IMAGEN CON GALERIA */
        if (requestCode == Gallery_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile=null;
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == Gallery_REQUEST_CODE2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2=null;
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode==PHOTO_REQUEST_CODE && resultCode== RESULT_OK){
            mImageFile=null;
            mPhotoFile= new File(mAbsolutePhotoPatch);
            Picasso.with(PostActivity.this).load(mPhotoPatch).into(mImageViewPost1);
        }
        if (requestCode==PHOTO_REQUEST_CODE2 && resultCode== RESULT_OK){
            mImageFile2=null;
            mPhotoFile2= new File(mAbsolutePhotoPatch2);
            Picasso.with(PostActivity.this).load(mPhotoPatch2).into(mImageViewPost2);
        }

    }

}