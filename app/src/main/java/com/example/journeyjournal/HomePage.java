package com.example.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomePage";

    private static final String KEY_FIRSTNAME = "firstName";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DocumentReference docRef = db.collection("users").document(userID);


    ListView listView;
    ArrayList<Data> dataArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);




        Button signOut = (Button) findViewById(R.id.signOut);
        Button add_journey = (Button) findViewById(R.id.add_journey);
        signOut.setOnClickListener(this);
        add_journey.setOnClickListener(this);

        listView = findViewById(R.id.listView);
        dataArrayList = new ArrayList<>();

        loadDataInListView();
        loadName();


    }






    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signOut:
                Intent intent_signOut = new Intent(HomePage.this, MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent_signOut);
                break;
            case R.id.add_journey:
                Intent intent_add = new Intent(HomePage.this, AddJourney.class);
                startActivity(intent_add);
                break;
        }
    }


    private void loadName() {
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


    }

    private void loadDataInListView() {
        db.collection("users").document(userID).collection("journeys").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Data data = d.toObject(Data.class);

                                dataArrayList.add(data);
                            }

                            Adapter adapter = new Adapter(HomePage.this, dataArrayList);

                            listView.setAdapter(adapter);

                        } else {
                            Toast.makeText(HomePage.this, "No data found in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}