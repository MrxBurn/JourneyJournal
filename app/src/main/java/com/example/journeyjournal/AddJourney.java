package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;


public class AddJourney extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText description;
    private ImageView journeyIMG;
    private Button uploadCamera;
    private Button uploadGallery;


    private Button add;

    String jTitle;
    String jDescription;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static int CODE = 2;


    FirebaseFirestore fStore = FirebaseFirestore.getInstance();


    private StorageReference storageReference;
    StorageReference filepath;
    Uri uri;

    DocumentReference docRef = fStore.collection("users").document(userID).collection("journeys").document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);


        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        journeyIMG = (ImageView) findViewById(R.id.image);


        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);

        uploadCamera = (Button) findViewById(R.id.upload_camera);
        uploadCamera.setOnClickListener(this);

        uploadGallery = (Button) findViewById(R.id.upload_gallery);
        uploadGallery.setOnClickListener(this);


        storageReference = FirebaseStorage.getInstance().getReference();




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                addJourney();
                Intent intent = new Intent(AddJourney.this, HomePage.class);
                intent.setFlags(0);
                startActivityForResult(intent, 78);
                break;
            case R.id.upload_camera:
                imgCamera();


                break;
            case R.id.upload_gallery:
                imgGallery();
        }


    }

    private void imgGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 0);


    }

    private void imgCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    journeyIMG.setImageURI(uri);

                    filepath = storageReference.child(userID).child(uri.getLastPathSegment());

                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {

                    uri = data.getData();

                    filepath = storageReference.child(userID).child(uri.getLastPathSegment());

                    break;

                }
        }
    }


    private void addJourney() {
        jTitle = title.getText().toString();
        jDescription = description.getText().toString();


        Map<String, String> data = new HashMap<>();

        data.put("title", jTitle);
        data.put("description", jDescription);
        data.put("journeyID", null);
        data.put("imgUrl", null);

        fStore.collection("users").document(userID).collection("journeys").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddJourney.this, "Successfully added journey!", Toast.LENGTH_SHORT);

                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        fStore.collection("users").document(userID).collection("journeys").document(documentReference.getId()).update(
                                                "imgUrl", uri.toString(),
                                                "journeyID", documentReference.getId());
                                    }
                                });
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddJourney.this, "Failed to add data", Toast.LENGTH_SHORT).show();
            }
        });


    }



}





