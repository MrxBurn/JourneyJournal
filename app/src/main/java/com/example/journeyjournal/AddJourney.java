package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddJourney extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText description;

    private Button add;

    String jTitle;
    String jDescription;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        fStore = FirebaseFirestore.getInstance();

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);

        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        addJourney();
    }

    private void addJourney() {
        jTitle = title.getText().toString();
        jDescription = description.getText().toString();


        Map<String, String> data = new HashMap<>();

        data.put("title", jTitle);
        data.put("description", jDescription);

        fStore.collection("users").document(userID).collection("journeys").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddJourney.this, "Successfully added journey!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(AddJourney.this, HomePage.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddJourney.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                    }
                });



    }
}