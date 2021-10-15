package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomePage";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRSTNAME = "firstName";

    private TextView data;
    private Button signOut;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DocumentReference docRef = db.collection("users").document(userID);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        data = (TextView) findViewById(R.id.textView);
        signOut = (Button) findViewById(R.id.signOut);
        signOut.setOnClickListener(this);

        loadData();

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signOut:
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
        }
    }


    private void loadData() {
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String firstName = documentSnapshot.getString(KEY_FIRSTNAME);
                            String email = documentSnapshot.getString(KEY_EMAIL);

                            data.setText("Hello " + firstName + "\n" + "Email: " + email);

                        }
                        else {
                            Toast.makeText(HomePage.this, "Failed fetching data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this, "No data on this user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });


    }
}