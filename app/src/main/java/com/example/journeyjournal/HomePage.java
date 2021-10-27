package com.example.journeyjournal;

import static com.example.journeyjournal.AddJourney.CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity implements View.OnClickListener {


    private static final String KEY_FIRSTNAME = "firstName";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DocumentReference docRef = db.collection("users").document(userID);


    RecyclerView recyclerView;
    ArrayList<Data> dataArrayList;

    CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        Button signOut = (Button) findViewById(R.id.signOut);
        Button add_journey = (Button) findViewById(R.id.add_journey);
        signOut.setOnClickListener(this);
        add_journey.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataArrayList = new ArrayList<Data>();

        adapter = new CustomAdapter(HomePage.this, dataArrayList);

        recyclerView.setAdapter(adapter);


        loadDataInListView();


        // loadName();


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signOut:
                Intent intent_signOut = new Intent(HomePage.this, MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent_signOut);
                break;
            case R.id.add_journey:
                Intent intent_add = new Intent(HomePage.this, AddJourney.class);
                startActivityForResult(intent_add, CODE);
                break;

            case R.id.refresh:
                overridePendingTransition(0, 0);
                Intent i = new Intent(HomePage.this, HomePage.class);
                overridePendingTransition(0, 0);
                startActivity(i);
        }
    }


  /*  private void loadName() {
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String firstName = documentSnapshot.getString(KEY_FIRSTNAME);

                            //Todo: add welcome and name


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


    }*/


    private void loadDataInListView() {
        db.collection("users").document(userID).collection("journeys").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d("Error", error.getMessage());
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {

                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        dataArrayList.add(dc.getDocument().toObject(Data.class));
                    }

                    if (dc.getType() == DocumentChange.Type.MODIFIED) {
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        finish();
                    }

                    adapter.notifyDataSetChanged();


                }

            }
        });


    }
}