package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class littleaboutyou extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_littleaboutyou);
        EditText nicknameField = findViewById(R.id.fieldNickname);
        EditText statusField = findViewById(R.id.fieldStatus);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameField.getText().toString();
                String status = statusField.getText().toString();
                makeCollection(nickname, status);
            }
        });

    }
    private void makeCollection(String nickname, String status) {
        EditText nicknameField = findViewById(R.id.fieldNickname);
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
        Intent intent = new Intent(littleaboutyou.this, listofdialogs.class);
        startActivity(intent);
    }
}