package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditJourney extends AppCompatActivity implements View.OnClickListener {

    //Firebase
    FirebaseFirestore db;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Variables
    Bundle extras;
    AlertDialog.Builder builder;

    String jTitle;
    String jDescription;
    String jImgUrl;
    String jJourneyID;
    String[] options = {"Camera", "Gallery"};
    String choice = "none";

    StorageReference storageReference;
    StorageReference filepath;

    Intent intent;
    Intent takePicture;

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    byte[] b = stream.toByteArray();

    Uri uri;

    //Views & Buttons
    EditText editTextTitle;
    EditText editTextDescription;
    ImageView imageViewImage;

    Button editJourney;
    Button deleteJourney;
    ImageButton share;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        //Instantiate storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Intent to be used after button clicks
        intent = new Intent(EditJourney.this, HomePage.class);

        //ProgressDialog instantiation
        progressDialog = new ProgressDialog(this);


        //Builder instantiation
        builder = new AlertDialog.Builder(this);

        //Instantiate Firebase
        db = FirebaseFirestore.getInstance();

        //Get the journey data after clicking on it from the main page
        extras = getIntent().getExtras();
        if (extras != null) {
            jTitle = extras.getString("title");
            jDescription = extras.getString("description");
            jImgUrl = extras.getString("imgUrl");
            jJourneyID = extras.getString("journeyID");
        }

        //Instantiate the views
        editTextTitle = (EditText) findViewById(R.id.title);
        editTextDescription = (EditText) findViewById(R.id.description);
        imageViewImage = (ImageView) findViewById(R.id.journey_image);
        imageViewImage.setOnClickListener(this);

        editJourney = (Button) findViewById(R.id.edit);
        deleteJourney = (Button) findViewById(R.id.delete);
        share = (ImageButton) findViewById(R.id.share);

        //set on click method for buttons
        editJourney.setOnClickListener(this);
        deleteJourney.setOnClickListener(this);
        share.setOnClickListener(this);

        //Get the data from intent in the views
        editTextTitle.setText(jTitle);
        editTextDescription.setText(jDescription);

        Picasso.get().load(jImgUrl).into(imageViewImage);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit:
                editJourneyData();
                startActivity(intent);

                break;
            case R.id.delete:
                deleteDataJourney();
                startActivity(intent);
                break;
            case R.id.share:
                shareJourney();
                break;
            case R.id.journey_image:
                pickOption();
        }

    }

    private void deleteDataJourney() {


        progressDialog.setMessage("Deleting journey...");
        progressDialog.setTitle("Delete");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Get the document
        //Delete it
        db.collection("users").document(userID).collection("journeys").document(jJourneyID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditJourney.this, "Deleted journey successfully", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void editJourneyData() {

        String addNewTitle = editTextTitle.getText().toString();
        String addNewDescription = editTextDescription.getText().toString();

        progressDialog.setMessage("Updating journey details...");
        progressDialog.setTitle("Edit");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Get the document
        //Update it with the new content
        db.collection("users").document(userID).collection("journeys").document(jJourneyID)
                .update(
                        "title", addNewTitle,
                        "description", addNewDescription

                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(EditJourney.this, "Edited journey successfully", Toast.LENGTH_LONG).show();
                Log.d("JourneyData", "editTextData " + editTextTitle.toString() + editTextDescription.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("JourneyData", "editTextData " + editTextTitle.toString() + editTextDescription.toString());
                Log.d("JourneyData", "Exception " + e.getMessage());

            }
        });

        if (choice == "gallery") {
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            db.collection("users").document(userID).collection("journeys").document(jJourneyID).update("imgUrl", uri.toString());
                            Intent i = new Intent(EditJourney.this, EditJourney.class);
                            startActivity(i);
                        }
                    });
                }
            });
        }
        else if (choice == "camera"){
            filepath.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            db.collection("users").document(userID).collection("journeys").document(jJourneyID).update("imgUrl", uri.toString());
                            Intent i = new Intent(EditJourney.this, EditJourney.class);
                            startActivity(i);
                        }
                    });
                }
            });
        }


    }

    private void shareJourney() {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, jTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, jDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, jImgUrl);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Send journey"));


    }

    private void pickOption() {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    imageViewImage.setImageURI(uri);

                    Log.d("URICODE", uri.toString());

                    filepath = storageReference.child(userID).child(uri.getLastPathSegment());

                    choice = "gallery";

                    break;
                }
            case 1:
                if (resultCode == RESULT_OK) {

                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageViewImage.setImageBitmap(imageBitmap);

                    filepath = storageReference.child(userID).child(imageBitmap.toString());

                    stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    b = stream.toByteArray();

                    choice = "camera";
                }
        }
    }
}

