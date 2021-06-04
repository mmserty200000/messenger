package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class myprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        EditText nameedit = findViewById(R.id.editname);
        EditText statusedit = findViewById(R.id.editstatus);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nameedit.setText(document.getString("nickname"));
                        statusedit.setText(document.getString("status"));
                        ActionBar actionBar = getSupportActionBar();
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setTitle(document.getString("nickname"));
                    }
                } else {
                    Toast.makeText(myprofile.this, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button saveButton = findViewById(R.id.saveButton1);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nameedit.getText().toString();
                String status = statusedit.getText().toString();
                makeCollection(nickname, status);
            }
        });
        Button logoutButton = findViewById(R.id.logoutButton1);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(myprofile.this, log_in.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void makeCollection(String nickname, String status) {
        EditText nicknameField = findViewById(R.id.editname);
        if(TextUtils.isEmpty(nickname)){
            nicknameField.setError("Required.");
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", nickname);
        data.put("status", status);
        db.collection("users").document(user.getUid()).set(data);
        Intent intent = new Intent(myprofile.this, listofdialogs.class);
        startActivity(intent);
    }
}