package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddJourney extends AppCompatActivity implements View.OnClickListener {

    //Variables
    private EditText title;
    private EditText description;
    private ImageView journeyIMG;
    private Button uploadOption;


    private Button add;

    String jTitle;
    String jDescription;
    //Get current userID
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static int CODE = 2;
    String imgURL = null;

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");


    private StorageReference storageReference;
    StorageReference filepath;
    Uri uri;



    String[] options = {"Camera", "Gallery"};

    AlertDialog.Builder builder;
    Intent takePicture;


    DocumentReference docRef = fStore.collection("users").document(userID).collection("journeys").document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);


        //Instantiate all the required elements

        builder = new AlertDialog.Builder(this);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        journeyIMG = (ImageView) findViewById(R.id.image);


        uploadOption = (Button) findViewById(R.id.uploadOption);
        uploadOption.setOnClickListener(this);


        storageReference = FirebaseStorage.getInstance().getReference();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadOption:
                pickOption();
                break;

        }


    }

    private void pickOption() {

        //Select uploading image from gallery or camera

        builder.setTitle("Select option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);
                } else {
                    takePicture = new Intent(Intent.ACTION_GET_CONTENT);
                    takePicture.setType("image/*");
                    startActivityForResult(takePicture, 0);
                }
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If Gallery was selected -> upload all the data
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    journeyIMG.setImageURI(uri);

                    filepath = storageReference.child(userID).child(uri.getLastPathSegment());

                    jTitle = title.getText().toString();
                    jDescription = description.getText().toString();

                    //HashMap to hold all the data
                    Map<String, String> journey = new HashMap<>();

                    journey.put("title", jTitle);
                    journey.put("description", jDescription);
                    journey.put("journeyID", null);
                    journey.put("imgUrl", imgURL);
                    journey.put("date_time", dateFormat.format(new Date()));


                    //Upload image including the other relevant data
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    fStore.collection("users").document(userID).collection("journeys").add(journey)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(AddJourney.this, "Successfully added journey!", Toast.LENGTH_SHORT);

                                                    fStore.collection("users").document(userID).collection("journeys").document(documentReference.getId()).update("journeyID", documentReference.getId(),
                                                            "imgUrl", uri.toString());
                                                }
                                            });
                                }


                            });

                        }
                    });
                    break;
                }

                //If Camera was selected -> start uploading
            case 1:
                if (resultCode == RESULT_OK) {

                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    journeyIMG.setImageBitmap(imageBitmap);

                    filepath = storageReference.child(userID).child(imageBitmap.toString());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    jTitle = title.getText().toString();
                    jDescription = description.getText().toString();

                    //HashMap to hold the data
                    Map<String, String> journey = new HashMap<>();

                    journey.put("title", jTitle);
                    journey.put("description", jDescription);
                    journey.put("journeyID", null);
                    journey.put("imgUrl", imgURL);
                    journey.put("date_time", dateFormat.format(new Date()));

                    byte[] b = stream.toByteArray();

                    filepath.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    fStore.collection("users").document(userID).collection("journeys").add(journey)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(AddJourney.this, "Successfully added journey!", Toast.LENGTH_SHORT);

                                                    fStore.collection("users").document(userID).collection("journeys").document(documentReference.getId()).update("journeyID", documentReference.getId(),
                                                            "imgUrl", uri.toString());
                                                }
                                            });

                                }
                            });


                        }

                    });

                }
                break;
        }

    }
}

