package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditJourney extends AppCompatActivity implements View.OnClickListener {

    //Firebase
    FirebaseFirestore db;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Variables
    Bundle extras;

    String jTitle;
    String jDescription;
    String jImgUrl;
    String jJourneyID;

    Intent intent;

    //Views & Buttons
    EditText editTextTitle;
    EditText editTextDescription;
    ImageView imageViewImage;

    Button editJourney;
    Button deleteJourney;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        //Intent to be used after button clicks
        intent = new Intent(EditJourney.this, HomePage.class);

        //ProgressDialog instantiation
        progressDialog = new ProgressDialog(this);



        //Instantiate Firebase
        db = FirebaseFirestore.getInstance();

        //Get the journey data after clicking on it from the main page
        extras = getIntent().getExtras();
        if(extras != null){
            jTitle = extras.getString("title");
            jDescription = extras.getString("description");
            jImgUrl = extras.getString("imgUrl");
            jJourneyID = extras.getString("journeyID");
        }

        //Instantiate the views
        editTextTitle = (EditText) findViewById(R.id.title);
        editTextDescription = (EditText) findViewById(R.id.description);
        imageViewImage = (ImageView) findViewById(R.id.journey_image);

        editJourney = (Button) findViewById(R.id.edit);
        deleteJourney = (Button) findViewById(R.id.delete);

        //set on click method for buttons
        editJourney.setOnClickListener(this);
        deleteJourney.setOnClickListener(this);

        //Get the data from intent in the views
        editTextTitle.setText(jTitle);
        editTextDescription.setText(jDescription);
        //Todo: add the image functionality





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit:
                editJourneyData();
                startActivity(intent);
                break;
            case R.id.delete:
                deleteDataJourney();
                startActivity(intent);
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
                Log.d("JourneyData", "editTextData " + editTextTitle.toString() + editTextDescription.toString() );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("JourneyData", "editTextData " + editTextTitle.toString() + editTextDescription.toString() );
                Log.d("JourneyData", "Exception " + e.getMessage());

            }
        });


    }
}