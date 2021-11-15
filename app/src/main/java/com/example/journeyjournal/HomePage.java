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
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class HomePage extends AppCompatActivity {

    //Variables & Firebase
    private static final String KEY_FIRSTNAME = "firstName";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DocumentReference docRef = db.collection("users").document(userID);


    RecyclerView recyclerView;
    ArrayList<Data> dataArrayList;

    CustomAdapter adapter;

    BottomNavigationView navBar;
    TextView welcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Instantiate variables
        welcome = (TextView) findViewById(R.id.welcome);

        navBar = (BottomNavigationView) findViewById(R.id.navBar);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataArrayList = new ArrayList<Data>();

        adapter = new CustomAdapter(HomePage.this, dataArrayList);

        recyclerView.setAdapter(adapter);


        loadDataInListView();


        loadName();

        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_1:
                        Intent intent_add = new Intent(HomePage.this, AddJourney.class);
                        startActivityForResult(intent_add, CODE);
                        break;

                    case R.id.page_2:
                        Intent intent_signOut = new Intent(HomePage.this, MainActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent_signOut);
                        break;

                }

            return true;
            }

        });





    }




    //Load the user's name functionality
    private void loadName() {
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String firstName = documentSnapshot.getString(KEY_FIRSTNAME);

                            welcome.setText("Welcome " + firstName + "!");


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
                        Log.d("No name", e.toString());
                    }
                });


    }

    //Load all the journeys in the list
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