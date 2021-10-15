package com.example.journeyjournal;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFirstName, editTextEmail, editTextPassword;
    ProgressDialog progressDialog;



    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore firestore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.firstName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.registerUser:
                registerUser();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString();

        if(firstName.isEmpty()){
            editTextFirstName.setError("First name is required");
            editTextFirstName.requestFocus();

        }

        else if(password.isEmpty()){
            editTextPassword.setError("Fill in password");
            editTextPassword.requestFocus();
        }

        else if(email.isEmpty()){
            editTextEmail.setError("Fill in email");
            editTextEmail.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();

        }

        else{

            progressDialog.setMessage("Registering...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                       progressDialog.dismiss();
                       Toast.makeText(RegisterUser.this,"Registration successful", Toast.LENGTH_LONG);

                       //store the data of user in database
                       userID = mAuth.getCurrentUser().getUid();
                       DocumentReference documentReference = firestore.collection("users").document(userID);

                       //Add user data to the database to his specific ID
                       Map<String, Object> user = new HashMap<>();
                       user.put("firstName", firstName);
                       user.put("email", email);

                       documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Log.d(TAG,"onSuccess: User profile is created for " + userID);
                           }
                       });

                       sendUserToHome();

                   }else {
                       progressDialog.dismiss();
                       Toast.makeText(RegisterUser.this,""+task.getException(), Toast.LENGTH_LONG);
                   }
                }
            });
        }







    }

    private void sendUserToHome() {
        Intent intent = new Intent(RegisterUser.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}